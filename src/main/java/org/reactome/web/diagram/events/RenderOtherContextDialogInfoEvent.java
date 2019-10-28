package org.reactome.web.diagram.events;

import org.reactome.web.diagram.context.dialogs.molecules.MoleculesTable;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.handlers.RenderOtherContextDialogInfoHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class RenderOtherContextDialogInfoEvent extends GwtEvent<RenderOtherContextDialogInfoHandler> {
    public static Type<RenderOtherContextDialogInfoHandler> TYPE = new Type<>();

    private MoleculesTable<GraphPhysicalEntity> table;
    
    public RenderOtherContextDialogInfoEvent(MoleculesTable<GraphPhysicalEntity> table) {
    	this.table = table;
    }
    
	@Override
	public Type<RenderOtherContextDialogInfoHandler> getAssociatedType() {
		return this.TYPE;
	}

	@Override
	protected void dispatch(RenderOtherContextDialogInfoHandler handler) {
		handler.onRenderOtherContextDialogInfo(this);
	}
	
	public MoleculesTable<GraphPhysicalEntity> getTable() {
		return table;
	}

}
