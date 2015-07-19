package org.reactome.web.diagram.key;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectsFactory;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.events.DatabaseObjectHoveredEvent;
import org.reactome.web.diagram.events.DatabaseObjectSelectedEvent;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.handlers.DatabaseObjectHoveredHandler;
import org.reactome.web.diagram.handlers.DatabaseObjectSelectedHandler;
import org.reactome.web.diagram.handlers.DiagramProfileChangedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.legends.ControlButton;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.Renderer;
import org.reactome.web.diagram.renderers.RendererManager;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.Console;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramKey extends AbsolutePanel implements DatabaseObjectHoveredHandler, DatabaseObjectSelectedHandler,
        DiagramRequestedHandler, DiagramProfileChangedHandler, ClickHandler {

    private EventBus eventBus;

    private Diagram diagram;
    private Double factor = 0.9;
    private Coordinate offset = CoordinateFactory.get(0, 0);

    private DiagramObject selected;

    private AdvancedContext2d hover;
    private AdvancedContext2d items;
    private AdvancedContext2d selection;
    private List<Canvas> canvases = new LinkedList<>();

    public DiagramKey(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().diagramKeyPanel());
        this.eventBus = eventBus;
        this.initHandlers();

        this.add(new InlineLabel("Diagram key"), 20, 5);
        this.add(new ControlButton("Close", RESOURCES.getCSS().close(), this));

        this.hover = this.createCanvas(250, 360);
        this.items = this.createCanvas(250, 360);
        this.selection = this.createCanvas(250, 360);

        this.add(new Image(RESOURCES.diagramKey()), 30, 380);
        this.setVisible(false);

        try {
            diagram = DiagramObjectsFactory.getModelObject(Diagram.class, RESOURCES.diagramkeyJson().getText());
        } catch (DiagramObjectException e) {
            Console.error(e.getMessage());
        }
    }

    private void draw() {
        if (!isVisible()) return;

        this.clearDiagramKey();
        for (Node node : diagram.getNodes()) {
            Renderer renderer = RendererManager.get().getDiagramKeyRenderer(node);
            if (renderer != null) {
                renderer.setColourProperties(items, ColourProfileType.NORMAL);
                renderer.draw(items, node, factor, offset);
                renderer.setTextProperties(items, ColourProfileType.NORMAL);
                items.setFont(RendererProperties.getFont(10));
                renderer.drawText(items, node, factor, offset);
            } else {
                Console.error(node.getRenderableClass());
            }
        }
        for (Edge edge : diagram.getEdges()) {
            Renderer renderer = RendererManager.get().getDiagramKeyRenderer(edge);
            if (renderer != null){
                renderer.setColourProperties(items, ColourProfileType.NORMAL);
                renderer.draw(items, edge, factor, offset);
                renderer.setTextProperties(items, ColourProfileType.NORMAL);
                items.setFont(RendererProperties.getFont(10));
                renderer.drawText(items, edge, factor, offset);
            } else {
                Console.error(edge.getRenderableClass());
            }
        }
        for (Note note : diagram.getNotes()) {
            Renderer renderer = RendererManager.get().getDiagramKeyRenderer(note);
            if (renderer != null){
                renderer.setTextProperties(items, ColourProfileType.NORMAL);
                items.setFont(RendererProperties.getFont(10));
                renderer.drawText(items, note, factor, offset);
                Console.info(note.getDisplayName() + " drawn!");
            } else {
                Console.error(note.getRenderableClass());
            }
        }
        selection.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        highlight(this.selection, this.selected);
    }

    private DiagramObject getDiagramObject(DatabaseObject databaseObject) {
        if (databaseObject != null) {
            return databaseObject.getDiagramObjects().get(0);
        }
        return null;
    }

    private void highlight(AdvancedContext2d ctx, DiagramObject diagramObject) {
        if (diagramObject == null) return;
        String renderableClass = diagramObject.getRenderableClass();
        for (Node node : diagram.getNodes()) {
            if (node.getRenderableClass().equals(renderableClass)) {
                Renderer renderer = RendererManager.get().getDiagramKeyRenderer(node);
                if (renderer != null) {
                    renderer.highlight(ctx, node, factor, offset);
                    return;
                }
            }
        }
        for (Edge edge : diagram.getEdges()) {
            if(edge.getRenderableClass().equals(renderableClass)){
                Renderer renderer = RendererManager.get().getDiagramKeyRenderer(edge);
                if (renderer != null) {
                    ctx.save();
                    ctx.setLineWidth(ctx.getLineWidth()/1.5);
                    renderer.draw(ctx, edge, factor, offset);
                    ctx.restore();
                    return;
                }
            }
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(DatabaseObjectSelectedEvent.TYPE, this);
        this.eventBus.addHandler(DatabaseObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
    }

    @Override
    public void onClick(ClickEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onDatabaseObjectHovered(DatabaseObjectHoveredEvent event) {
        hover.setLineWidth(factor * 9);
        hover.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getHighlight());
        cleanCanvas(hover);
        DiagramObject diagramObject = getDiagramObject(event.getDatabaseObject());
        highlight(hover, diagramObject);
    }


    @Override
    public void onDatabaseObjectSelected(DatabaseObjectSelectedEvent event) {
        selection.setLineWidth(factor * 3);
        selection.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        cleanCanvas(selection);
        this.selected = getDiagramObject(event.getDatabaseObject());
        highlight(selection, this.selected);
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {

    }

    @Override
    public void onProfileChanged(DiagramProfileChangedEvent event) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                draw();
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            addStyleName(RESOURCES.getCSS().diagramKeyPanelExpanded());
            draw();
            (new Timer() { //Timer is set due to a problem in firefox to render while css animation
                @Override
                public void run() {
                    draw();
                }
            }).schedule(300);
        } else {
            removeStyleName(RESOURCES.getCSS().diagramKeyPanelExpanded());
        }
    }

    private void cleanCanvas(Context2d ctx) {
        ctx.clearRect(0, 0, ctx.getCanvas().getWidth(), ctx.getCanvas().getHeight());
    }

    private void clearDiagramKey() {
        for (Canvas canvas : canvases) {
            cleanCanvas(canvas.getContext2d());
        }
    }

    private AdvancedContext2d createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 30);
        this.canvases.add(canvas);
        return canvas.getContext2d().cast();
    }



    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("data/diagramkey.json")
        TextResource diagramkeyJson();

        @Source("images/diagramKey.png")
        ImageResource diagramKey();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_disabled.png")
        ImageResource closeDisabled();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-DiagramKey")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/key/DiagramKey.css";

        String diagramKeyPanel();

        String diagramKeyPanelExpanded();

        String close();
    }
}
