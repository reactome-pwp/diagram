package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsResourceChangedEvent extends GwtEvent<InteractorsResourceChangedHandler> {
    public static final Type<InteractorsResourceChangedHandler> TYPE = new Type<>();

    String resource; //TODO: Change this for the proper InteractorResource object when it is created


    public InteractorsResourceChangedEvent(String resource) {
        this.resource = resource;
    }

    @Override
    public Type<InteractorsResourceChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsResourceChangedHandler handler) {
        handler.onInteractorsResourceChanged(this);
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorResourceChangedEvent{" +
                "resource='" + resource + '\'' +
                '}';
    }
}
