package org.reactome.web.diagram.util.svg.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.util.svg.events.SVGPanZoomEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public interface SVGPanZoomHandler extends EventHandler {

    void onSVGPanZoom(SVGPanZoomEvent event);

}