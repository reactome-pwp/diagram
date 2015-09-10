package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramExportRequestedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramExportRequestedEvent extends GwtEvent<DiagramExportRequestedHandler> {
    public static Type<DiagramExportRequestedHandler> TYPE = new Type<>();

    @Override
    public Type<DiagramExportRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramExportRequestedHandler handler) {
        handler.onDiagramExportRequested(this);
    }

    @Override
    public String toString() {
        return "DiagramRequestEvent{}";
    }
}
