package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.handlers.InteractorSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorSelectedEvent extends GwtEvent<InteractorSelectedHandler> {
    public static final Type<InteractorSelectedHandler> TYPE = new Type<>();

    private DiagramInteractor interactor;

    public InteractorSelectedEvent(DiagramInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public Type<InteractorSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorSelectedHandler handler) {
        handler.onInteractorSelected(this);
    }

    public DiagramInteractor getInteractor() {
        return interactor;
    }

    @Override
    public String toString() {
        return "InteractorSelectedEvent{" +
                "interactor=" + interactor +
                '}';
    }
}
