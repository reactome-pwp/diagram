package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsToggledHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsToggledEvent extends GwtEvent<InteractorsToggledHandler> {
    public static final Type<InteractorsToggledHandler> TYPE = new Type<>();

    private boolean visible;

    public InteractorsToggledEvent(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Type<InteractorsToggledHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsToggledHandler handler) {
        handler.onInteractorsToggled(this);
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return "InteractorsToggledEvent{" +
                "visible=" + visible +
                '}';
    }
}
