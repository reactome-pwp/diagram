package org.reactome.web.diagram.util.svg.thumbnail;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.util.svg.AbstractSVGPanel;
import org.reactome.web.diagram.util.svg.events.SVGLoadedEvent;
import org.reactome.web.diagram.util.svg.events.SVGPanZoomEvent;
import org.reactome.web.diagram.util.svg.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.util.svg.handlers.SVGLoadedHandler;
import org.reactome.web.diagram.util.svg.handlers.SVGPanZoomHandler;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGThumbnail extends AbstractSVGPanel implements DiagramLoadRequestHandler, DiagramLoadedHandler,
        SVGLoadedHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler,
        SVGPanZoomHandler {
    private static final int HEIGHT = 75;
    private static final int FALLBACK_WIDTH = 100;

    private Canvas frame;
    private OMSVGPoint from;
    private OMSVGPoint to;

    private OMSVGPoint mouseDown;
    private OMSVGPoint delta;

    public SVGThumbnail(EventBus eventBus) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGThumbnail");

        this.frame = this.createCanvas(0, 0);

        this.setStyle();
        this.initHandlers();
        this.initListeners();
    }

    public void clearThumbnail() {
        cleanFrame();
        setVisible(false);
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        svg.getElement().removeFromParent();
        svg = null;
        clearThumbnail();
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        svg.getElement().removeFromParent();
        svg = null;
        clearThumbnail();
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation(); event.preventDefault();
        Element elem = event.getRelativeElement();
        OMSVGPoint p = svg.createSVGPoint(event.getRelativeX(elem), event.getRelativeY(elem));
        if (isMouseInVisibleArea(p)) {
            mouseDown = svg.createSVGPoint(p);
            delta = svg.createSVGPoint(mouseDown.substract(from));
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.stopPropagation(); event.preventDefault();
        Element elem = event.getRelativeElement();
        OMSVGPoint p = svg.createSVGPoint(event.getRelativeX(elem), event.getRelativeY(elem));
        if (mouseDown != null) {
            if (from != null && to != null) {
                //Do not change any property of the status since it will be updated once the corresponding
                //action is performed in the main view and notified (thumbnail status changes on demand)
                OMSVGPoint padding = from.substract(p.substract(delta));
                eventBus.fireEventFromSource(new SVGThumbnailAreaMovedEvent(padding), this);
            }
        } else {
            if (isMouseInVisibleArea(p)) {
                getElement().getStyle().setCursor(Style.Cursor.MOVE);
            } else {
                getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation(); event.preventDefault();
        this.mouseDown = null;
    }


    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.stopPropagation(); event.preventDefault();
        this.mouseDown = null;
    }

    @Override
    public void onSVGLoaded(SVGLoadedEvent event) {
        svg = (OMSVGSVGElement) event.getSVG().cloneNode(true);

        from = svg.createSVGPoint();
        to = svg.createSVGPoint();

        OMSVGRect svgSize = getSVGInitialSize();
        double factor = HEIGHT / (svgSize.getHeight() + FRAME);
        setSize((int) Math.ceil(factor * (svgSize.getWidth() + FRAME)), HEIGHT);

        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);

        Element div = getElement();
        if(div.getChildCount() == 1) {
            // Only the canvas is added
            div.insertFirst(svg.getElement());
        } else {
            // both the canvas and the svg have been added
            div.replaceChild(svg.getElement(), div.getFirstChild());
        }

        // Identify all layers by getting all top-level g elements
        svgLayers = getRootLayers();

        // Set initial translation matrix
        initialTM = svg.getCTM();
        initialBB = svg.getBBox();

        OMSVGMatrix fitTM = calculateFitAll(FRAME);
        ctm = initialTM.multiply(fitTM);
        applyCTM(false);
    }

    @Override
    public void onSVGPanZoom(SVGPanZoomEvent event) {
        from = event.getFrom();
        to = event.getTo();

        from = from.matrixTransform(ctm);
        to = to.matrixTransform(ctm);

        drawFrame(from, to);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        frame.setCoordinateSpaceWidth(width);
        frame.setCoordinateSpaceHeight(height);
        frame.setPixelSize(width, height);

        Context2d ctx = this.frame.getContext2d();
        ctx.setStrokeStyle("#000000");
        ctx.setFillStyle("rgba(200,200,200, 0.4)");
        ctx.setLineWidth(0.5);
    }

    private void cleanFrame() {
        frame.getContext2d().clearRect(0, 0, frame.getOffsetWidth(), frame.getOffsetHeight());
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 0);
        return canvas;
    }

    private void drawFrame(OMSVGPoint from, OMSVGPoint to) {
        float tX = from.getX();
        float tY = from.getY();
        float width = to.getX() - tX;
        float height = to.getY() - tY;

        cleanFrame();

        Context2d ctx = this.frame.getContext2d();
        ctx.fillRect(0, 0, this.frame.getOffsetWidth(), this.frame.getOffsetHeight());
        ctx.clearRect(tX, tY, width, height);
        ctx.strokeRect(tX, tY, width, height);
    }

    private OMSVGRect getSVGInitialSize() {
        return svg.getViewBox().getBaseVal()!=null ? svg.getViewBox().getBaseVal() : svg.createSVGRect(0, 0, FALLBACK_WIDTH, HEIGHT);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(SVGLoadedEvent.TYPE, this);
        eventBus.addHandler(SVGPanZoomEvent.TYPE, this);
    }

    private void initListeners() {
        this.frame.addMouseDownHandler(this);
        this.frame.addMouseMoveHandler(this);
        this.frame.addMouseUpHandler(this);
        this.frame.addMouseOutHandler(this);
    }

    private boolean isMouseInVisibleArea(OMSVGPoint mouse) {
        return mouse.getX() >= this.from.getX()
                && mouse.getY() >= this.from.getY()
                && mouse.getX() <= this.to.getX()
                && mouse.getY() <= this.to.getY();
    }

    private void setStyle() {
        Style style = this.getElement().getStyle();
        style.setBackgroundColor("blue");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
    }
}
