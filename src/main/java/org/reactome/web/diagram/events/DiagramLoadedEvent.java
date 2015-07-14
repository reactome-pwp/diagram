package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramLoadedEvent extends GwtEvent<DiagramLoadedHandler> {
    public static Type<DiagramLoadedHandler> TYPE = new Type<DiagramLoadedHandler>();

    private DiagramContext context;

    public DiagramLoadedEvent(DiagramContext context) {
        this.context = context;
    }

    @Override
    public Type<DiagramLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramLoadedHandler handler) {
        handler.onDiagramLoaded(this);
    }

    public DiagramContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "DiagramLoadedEvent{" +
                "context=" + context +
                '}';
    }
}
