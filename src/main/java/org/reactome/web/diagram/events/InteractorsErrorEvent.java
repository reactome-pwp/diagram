package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsErrorHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsErrorEvent extends GwtEvent<InteractorsErrorHandler> {
    public static final Type<InteractorsErrorHandler> TYPE = new Type<>();

    private String message;

    public InteractorsErrorEvent(String message) {
        this.message = message;
    }

    @Override
    public Type<InteractorsErrorHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsErrorHandler handler) {
        handler.onInteractorsError(this);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "InteractorsErrorEvent{" +
                "message='" + message + '\'' +
                '}';
    }
}
