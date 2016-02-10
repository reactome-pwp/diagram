package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsErrorHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsErrorEvent extends GwtEvent<InteractorsErrorHandler> {
    public static final Type<InteractorsErrorHandler> TYPE = new Type<>();

    private String message;
    private String resource;

    public InteractorsErrorEvent(String resource, String message) {
        this.resource = resource;
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

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorsErrorEvent{" +
                "resource='" + resource + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
