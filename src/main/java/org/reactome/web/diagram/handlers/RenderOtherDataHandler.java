package org.reactome.web.diagram.handlers;

import org.reactome.web.diagram.events.RenderOtherDataEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface RenderOtherDataHandler extends EventHandler{
	void onRenderOtherData(RenderOtherDataEvent event);
}
