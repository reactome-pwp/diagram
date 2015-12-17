package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorResourceChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorResourceChangedEvent extends GwtEvent<InteractorResourceChangedHandler> {
    public static final Type<InteractorResourceChangedHandler> TYPE = new Type<>();

    String resource; //TODO: Change this for the proper InteractorResource object when it is created


    public InteractorResourceChangedEvent(String resource) {
        this.resource = resource;
    }

    @Override
    public Type<InteractorResourceChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorResourceChangedHandler handler) {
        handler.onInteractorResourceChanged(this);
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
