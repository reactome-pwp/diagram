package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsResourceChangedEvent extends GwtEvent<InteractorsResourceChangedHandler> {
    public static final Type<InteractorsResourceChangedHandler> TYPE = new Type<>();

    OverlayResource resource;

    public InteractorsResourceChangedEvent(OverlayResource resource) {
        this.resource = resource;
    }

//    public InteractorsResourceChangedEvent() {
//        this.resource = DiagramFactory.INTERACTORS_INITIAL_RESOURCE;
//        this.name = DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME;
//        this.resourceType = ResourceType.STATIC;
//    }
//
//    public InteractorsResourceChangedEvent(String resource) {
//        this.resource = resource;
//        this.name = resource;
//        this.resourceType = ResourceType.PSICQUIC;
//    }
//
//    public InteractorsResourceChangedEvent(String token, String name) {
//        this.resource = token;
//        this.name = name;
//        this.resourceType = ResourceType.CUSTOM;
//    }

    @Override
    public Type<InteractorsResourceChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsResourceChangedHandler handler) {
        handler.onInteractorsResourceChanged(this);
    }

    public OverlayResource getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorsResourceChangedEvent{" +
                "resource=" + resource.toString() +
                '}';
    }
}
