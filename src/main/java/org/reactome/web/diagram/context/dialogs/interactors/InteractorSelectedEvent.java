package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InteractorSelectedEvent<T extends RawInteractor> extends GwtEvent<InteractorSelectedHandler> {
    public static Type<InteractorSelectedHandler> TYPE = new Type<>();

    private T value;

    public InteractorSelectedEvent(T value) {
        this.value = value;
    }

    @Override
    public Type<InteractorSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorSelectedHandler handler) {
        handler.onInteractorSelected(this);
    }

    public T getValue() {
        return value;
    }
}
