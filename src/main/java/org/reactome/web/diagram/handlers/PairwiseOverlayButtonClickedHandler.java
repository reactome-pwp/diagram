package org.reactome.web.diagram.handlers;

import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseOverlayButtonClickedHandler extends EventHandler{
	void onPairwiseOverlayButtonClicked(PairwiseOverlayButtonClickedEvent event);
}
