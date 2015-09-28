package org.reactome.web.diagram.tooltips;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.Objects;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TooltipContainer extends AbsolutePanel implements DiagramRequestedHandler, DiagramLoadedHandler,
        GraphObjectHoveredHandler, EntityDecoratorHoveredHandler,
        DiagramZoomHandler, DiagramPanningHandler {

    private static final int DELAY = 500;
    private static final double ZOOM_THRESHOLD = 1.2;

    private EventBus eventBus;
    private DiagramContext context;
    private Object hovered;

    private Timer hoveredTimer;

    private int width;
    private int height;

    public TooltipContainer(EventBus eventBus, int width, int height) {
        this.eventBus = eventBus;
        this.hoveredTimer = new Timer() {
            @Override
            public void run() {
                showTooltipExecute();
            }
        };
        setWidth(width);
        setHeight(height);
        initHandlers();
    }

    private void initHandlers() {
        this.eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(EntityDecoratorHoveredEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramPanningEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void onEntityDecoratorHovered(final EntityDecoratorHoveredEvent event) {
        if (event.getAttachment() != null) {
            setHovered(event.getAttachment());
        } else if (event.getSummaryItem() != null) {
            setHovered(event.getSummaryItem());
        } else {
            setHovered(event.getDiagramObject());
        }
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        boolean isDecorator = (hovered instanceof NodeAttachment || hovered instanceof SummaryItem);
        if(!isDecorator || event.getHoveredObject()==null) {
            setHovered(event.getHoveredObject());
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.context = event.getContext();
    }

    @Override
    public void onDiagramPanningEvent(DiagramPanningEvent event) {
        Tooltip tooltip = Tooltip.getTooltip();
        if (tooltip.isVisible()) {
            tooltip.hide();
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.context = null;
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        showTooltip();
    }

    public void setWidth(int width) {
        setWidth(width + "px");
        this.width = width;
    }

    public void setHeight(int height) {
        setHeight(height + "px");
        this.height = height;
    }

    private void setHovered(Object hovered){
        if(Objects.equals(this.hovered, hovered)) return;
        if (this.hoveredTimer.isRunning()) {
            this.hoveredTimer.cancel();
        }
        this.hovered = hovered;
        if (this.hovered == null) {
            showTooltipExecute(); //this will quickly hide the tooltip ;)
        } else {
            this.hoveredTimer.schedule(DELAY);
        }
    }

    private void showTooltip() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                showTooltipExecute();
            }
        });
    }

    private void showTooltipExecute() {
        Tooltip tooltip = Tooltip.getTooltip();
        if (hovered == null || context == null) {
            tooltip.hide();
        } else {
            Coordinate offset = context.getDiagramStatus().getOffset();
            double factor = context.getDiagramStatus().getFactor();
            if (hovered instanceof Node) {
                if (factor > ZOOM_THRESHOLD) {
                    tooltip.hide();
                    return;
                }
                Node node = (Node) hovered;
                tooltip.setText(node.getDisplayName());
                NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
                tooltip.setPositionAndShow(this, prop.getX(), prop.getY(), prop.getHeight() + 8.0 * factor);
            } else if (hovered instanceof Edge) {
                Edge edge = (Edge) hovered;
                tooltip.setText(edge.getDisplayName());
                Shape shape = ShapeFactory.transform(edge.getReactionShape(), factor, offset);
                String type = shape.getType();
                if (type.equals("BOX")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getA().getX(),
                            shape.getA().getY(),
                            (shape.getB().getY() - shape.getA().getY()) + 8.0 * factor
                    );
                } else if (type.equals("CIRCLE") || type.equals("DOUBLE_CIRCLE")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getC().getX() - shape.getR(),
                            shape.getC().getY(),
                            shape.getR() + 8.0 * factor
                    );
                }
            } else if (hovered instanceof NodeAttachment){
                NodeAttachment attachment = (NodeAttachment) hovered;
                tooltip.setText(attachment.getDescription());
                Shape shape = ShapeFactory.transform(attachment.getShape(), factor, offset);
                String type = shape.getType();
                if (type.equals("BOX")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getA().getX(),
                            shape.getA().getY(),
                            (shape.getB().getY() - shape.getA().getY()) + 4.0 * factor
                    );
                }
            } else if (hovered instanceof SummaryItem){
                SummaryItem summaryItem = (SummaryItem) hovered;
                Shape shape = ShapeFactory.transform(summaryItem.getShape(), factor, offset);
                tooltip.setText(shape.getS() + " interactions");
                String type = shape.getType();
                if (type.equals("CIRCLE")) {
                    tooltip.setPositionAndShow(
                            this,
                            shape.getC().getX() - shape.getR(),
                            shape.getC().getY(),
                            shape.getR() + 8.0 * factor
                    );
                }
            } else {
                tooltip.hide(); //just in case :)
            }
        }
    }
}
