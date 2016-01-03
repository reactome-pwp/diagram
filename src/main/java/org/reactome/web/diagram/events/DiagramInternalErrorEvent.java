package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramInternalErrorHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramInternalErrorEvent extends GwtEvent<DiagramInternalErrorHandler> {
    public static final Type<DiagramInternalErrorHandler> TYPE = new Type<>();

    private String message;

    public DiagramInternalErrorEvent(String message) {
        this.message = message;
    }

    @Override
    public Type<DiagramInternalErrorHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramInternalErrorHandler handler) {
        handler.onDiagramInternalError(this);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DiagramInternalErrorEvent{" +
                "message='" + message + '\'' +
                '}';
    }
}
