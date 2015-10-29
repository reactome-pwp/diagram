package org.reactome.web.diagram.thumbnail;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.Compartment;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.thumbnail.render.ThumbnailRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramThumbnail extends AbsolutePanel implements GraphObjectSelectedHandler, GraphObjectHoveredHandler,
        DiagramRenderedHandler, DiagramZoomHandler, DiagramPanningHandler, ViewportResizedHandler, DiagramRequestedHandler,
        MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler,
        DiagramProfileChangedHandler {

    private static final int FRAME = 26;
    private static final int HEIGHT = 75;
    private static final double MIN_LINE_WIDTH = 0.15;

    DiagramContent content;
    Coordinate offset;
    double factor;
    Coordinate from;
    Coordinate to;
    Coordinate mouseDown = null;
    Coordinate delta = null;

    private Canvas compartments;
    private Canvas items;
    private Canvas highlight;
    private Canvas selection;
    private Canvas frame;
    private List<Canvas> canvases = new LinkedList<>();

    private EventBus eventBus;

    public DiagramThumbnail(EventBus eventBus) {
        this.eventBus = eventBus;
        this.initHandlers();

        this.compartments = this.createCanvas(0, 0);
        this.items = this.createCanvas(0, 0);
        this.highlight = this.createCanvas(0, 0);
        this.selection = this.createCanvas(0, 0);
        this.frame = this.createCanvas(0, 0);

        this.initListeners();
        this.setStyle();
    }

    private void initHandlers() {
        this.eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRenderedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        this.eventBus.addHandler(DiagramPanningEvent.TYPE, this);
        this.eventBus.addHandler(ViewportResizedEvent.TYPE, this);
    }

    private void initListeners() {
        this.frame.addMouseDownHandler(this);
        this.frame.addMouseMoveHandler(this);
        this.frame.addMouseUpHandler(this);
        this.frame.addMouseOutHandler(this);
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        this.cleanCanvas(this.selection);
        GraphObject graphObject = event.getGraphObject();
        if(graphObject !=null) {
            for (DiagramObject selected : graphObject.getDiagramObjects()) {
                this.select(selected);
            }
        }
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        this.cleanCanvas(this.highlight);
        GraphObject hovered = event.getGraphObject();
        List<DiagramObject> toHover = hovered!=null?hovered.getDiagramObjects():new LinkedList<DiagramObject>();
        for (DiagramObject item : toHover) {
            this.highlight(item);
        }
    }

    @Override
    public void onProfileChanged(DiagramProfileChangedEvent event) {
        this.setCanvasProperties();
        this.drawThumbnail();
    }

    @Override
    public void onDiagramRendered(DiagramRenderedEvent event) {
        this.setContent(event.getContent(), event.getVisibleArea());
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.content = null;
        this.clearThumbnail();
    }

    @Override
    public void onDiagramPanningEvent(DiagramPanningEvent event) {
        this.setVisibleArea(event.getVisibleArea());
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        this.setVisibleArea(event.getVisibleArea());
    }


    @Override
    public void onViewportResized(ViewportResizedEvent event) {
        this.setVisibleArea(event.getVisibleArea());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        Element elem = event.getRelativeElement();
        Coordinate c = CoordinateFactory.get(event.getRelativeX(elem), event.getRelativeY(elem));
        if (this.isMouseInVisibleArea(c)) {
            this.mouseDown = c;
            this.delta = this.mouseDown.minus(this.from);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.stopPropagation();
        event.preventDefault();
        Element elem = event.getRelativeElement();
        Coordinate mouse = CoordinateFactory.get(event.getRelativeX(elem), event.getRelativeY(elem));
        if (this.mouseDown != null) {
            if (this.from != null && this.to != null) {
                //Do not change any property of the status since it will be updated once the corresponding
                //action is performed in the main view and notified (thumbnail status changes on demand)
                Double factor = Math.ceil(this.factor); //Fix for unstable thumbnail dragging behaviour
                Coordinate padding = this.from.minus(mouse.minus(this.delta)).divide(factor);
                this.eventBus.fireEventFromSource(new ThumbnailAreaMovedEvent(padding), this);
            }
        } else {
            if (this.isMouseInVisibleArea(mouse)) {
                getElement().getStyle().setCursor(Style.Cursor.MOVE);
            } else {
                getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.mouseDown = null;
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.mouseDown = null;
    }

    public void clearThumbnail() {
        for (Canvas canvas : this.canvases) {
            this.cleanCanvas(canvas);
        }
        this.setVisible(false);
    }

    private void cleanCanvas(Canvas canvas) {
        canvas.getContext2d().clearRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 0);
        this.canvases.add(canvas);
        return canvas;
    }

    private void drawThumbnail() {
        this.setVisible(true);

        AdvancedContext2d compartments = this.compartments.getContext2d().cast();
        AdvancedContext2d items = this.items.getContext2d().cast();

        this.cleanCanvas(this.items);
        for (DiagramObject item : this.content.getDiagramObjects()) {
            ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
            if (renderer == null) continue;
            if (item instanceof Compartment) {
                renderer.draw(compartments, item, this.factor, this.offset);
            } else {
                renderer.draw(items, item, this.factor, this.offset);
            }
        }
    }

    private void select(DiagramObject item) {
        if (item == null) return;
        AdvancedContext2d ctx = this.selection.getContext2d().cast();
        ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
        if (renderer != null) {
            renderer.highlight(ctx, item, this.factor, this.offset);
        }
    }

    private void highlight(DiagramObject item) {
        if (item == null) return;
        AdvancedContext2d ctx = this.highlight.getContext2d().cast();
        ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
        if (renderer != null) {
            renderer.highlight(ctx, item, this.factor, this.offset);
        }
    }

    private void setContent(DiagramContent content, Box visibleArea) {
        if (this.content == content) return;

        this.content = content;

        this.factor = HEIGHT / (this.content.getHeight() + FRAME);
        int width = (int) Math.ceil((this.content.getWidth() + FRAME) * this.factor);
        this.offset = CoordinateFactory.get(FRAME / 2 - content.getMinX(), FRAME / 2 - content.getMinY());

        this.resize(width, HEIGHT);
        this.items.getContext2d().setLineWidth(this.factor < MIN_LINE_WIDTH ? MIN_LINE_WIDTH : this.factor);

        this.setVisible(true);
        this.setVisibleArea(visibleArea);
        this.drawThumbnail();
        this.drawFrame();

        this.setCanvasProperties();
    }

    private void resize(int w, int h) {
        this.setWidth(w + "px");
        this.setHeight(h + "px");

        for (Canvas canvas : canvases) {
            canvas.setCoordinateSpaceWidth(w);
            canvas.setCoordinateSpaceHeight(h);
            canvas.setPixelSize(w, h);
        }

        Context2d ctx = this.frame.getContext2d();
        ctx.setStrokeStyle("#000000");
        ctx.setFillStyle("rgba(200,200,200, 0.4)");
        ctx.setLineWidth(0.5);
    }

    private void setStyle() {
        Style style = this.getElement().getStyle();
        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
    }

    private void drawFrame() {
        if (this.from == null || this.to == null) return;
        Coordinate diff = this.to.minus(this.from);
        cleanCanvas(this.frame);
        Context2d ctx = this.frame.getContext2d();
        ctx.fillRect(0, 0, this.frame.getOffsetWidth(), this.frame.getOffsetHeight());
        ctx.clearRect(this.from.getX(), this.from.getY(), diff.getX(), diff.getY());
        ctx.strokeRect(this.from.getX(), this.from.getY(), diff.getX(), diff.getY());
    }

    boolean isMouseInVisibleArea(Coordinate mouse) {
        return mouse.getX() >= this.from.getX()
                && mouse.getY() >= this.from.getY()
                && mouse.getX() <= this.to.getX()
                && mouse.getY() <= this.to.getY();
    }

    private void setCanvasProperties(){
        AdvancedContext2d ctx = this.highlight.getContext2d().cast();
        ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getHighlight());
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getHighlight());
        ctx.setLineWidth(1);

        ctx = this.selection.getContext2d().cast();
        ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        ctx.setLineWidth(1);
    }

    private void setVisibleArea(Box visibleArea) {
        Coordinate from = CoordinateFactory.get(visibleArea.getMinX(), visibleArea.getMinY());
        Coordinate to = CoordinateFactory.get(visibleArea.getMaxX(), visibleArea.getMaxY());
        this.updateViewport(from, to);
    }

    void updateViewport(Coordinate from, Coordinate to) {
        if (this.content != null) {
            this.from = from.add(this.offset).multiply(this.factor);
            this.to = to.add(this.offset).multiply(this.factor);
            this.drawFrame();
        }
    }
}
