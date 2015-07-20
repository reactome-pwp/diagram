package org.reactome.web.diagram.tooltips;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.handlers.DatabaseObjectHoveredHandler;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TooltipContainer extends AbsolutePanel implements DiagramLoadedHandler {
    private EventBus eventBus;

    private int width;
    private int height;

    public TooltipContainer(EventBus eventBus, int width, int height) {
        this.eventBus = eventBus;
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(int width) {
        setWidth(width + "px");
        this.width = width;
    }

    public void setHeight(int height) {
        setHeight(height + "px");
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {

    }
}
