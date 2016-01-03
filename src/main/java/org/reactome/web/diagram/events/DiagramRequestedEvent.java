package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramRequestedEvent extends GwtEvent<DiagramRequestedHandler> {
    public static Type<DiagramRequestedHandler> TYPE = new Type<DiagramRequestedHandler>();

    @Override
    public Type<DiagramRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramRequestedHandler handler) {
        handler.onDiagramRequested(this);
    }

    @Override
    public String toString() {
        return "DiagramRequestEvent{}";
    }
}
