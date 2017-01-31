package org.reactome.web.diagram.client.visualisers.ehld.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGPanZoomEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public interface SVGPanZoomHandler extends EventHandler {

    void onSVGPanZoom(SVGPanZoomEvent event);

}