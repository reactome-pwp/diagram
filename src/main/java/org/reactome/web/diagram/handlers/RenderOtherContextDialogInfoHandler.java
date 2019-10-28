package org.reactome.web.diagram.handlers;

import org.reactome.web.diagram.events.RenderOtherContextDialogInfoEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface RenderOtherContextDialogInfoHandler extends EventHandler{
	void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event);
}
