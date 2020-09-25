package org.reactome.web.diagram.events;

import org.reactome.web.diagram.context.dialogs.molecules.MoleculesTable;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.handlers.ProteinsTableUpdatedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class ProteinsTableUpdatedEvent extends GwtEvent<ProteinsTableUpdatedHandler> {
    public static Type<ProteinsTableUpdatedHandler> TYPE = new Type<>();

    private MoleculesTable<GraphPhysicalEntity> table;
    
    public ProteinsTableUpdatedEvent(MoleculesTable<GraphPhysicalEntity> table) {
    	this.table = table;
    }
    
	@Override
	public Type<ProteinsTableUpdatedHandler> getAssociatedType() {
		return this.TYPE;
	}

	@Override
	protected void dispatch(ProteinsTableUpdatedHandler handler) {
		handler.onRenderOtherContextDialogInfo(this);
	}
	
	public MoleculesTable<GraphPhysicalEntity> getTable() {
		return table;
	}

}
