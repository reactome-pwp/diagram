package org.reactome.web.diagram.handlers;

import org.reactome.web.diagram.events.ProteinsTableUpdatedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface ProteinsTableUpdatedHandler extends EventHandler{
	void onRenderOtherContextDialogInfo(ProteinsTableUpdatedEvent event);
}
