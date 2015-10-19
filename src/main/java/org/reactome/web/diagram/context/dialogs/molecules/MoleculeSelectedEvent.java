package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class MoleculeSelectedEvent<T extends GraphPhysicalEntity> extends GwtEvent<MoleculeSelectedHandler> {
    public static Type<MoleculeSelectedHandler> TYPE = new Type<>();

    private T value;

    public MoleculeSelectedEvent(T value) {
        this.value = value;
    }

    @Override
    public Type<MoleculeSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MoleculeSelectedHandler handler) {
        handler.onMoleculeSelected(this);
    }

    public T getValue() {
        return value;
    }
}
