package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.InteractorsStatus;
import org.reactome.web.diagram.handlers.InteractorsStatusChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsStatusChangedEvent extends GwtEvent<InteractorsStatusChangedHandler> {
    public static final Type<InteractorsStatusChangedHandler> TYPE = new Type<>();

    private InteractorsStatus interactorsStatus;

    public InteractorsStatusChangedEvent(InteractorsStatus interactorsStatus) {
        this.interactorsStatus = interactorsStatus;
    }

    @Override
    public Type<InteractorsStatusChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsStatusChangedHandler handler) {
        handler.onInteractorsStatusChangedEvent(this);
    }

    public InteractorsStatus getInteractorsStatus() {
        return interactorsStatus;
    }

    @Override
    public String toString() {
        return "InteractorsStatusChangedEvent{" +
                "interactorsStatus=" + interactorsStatus +
                '}';
    }
}
