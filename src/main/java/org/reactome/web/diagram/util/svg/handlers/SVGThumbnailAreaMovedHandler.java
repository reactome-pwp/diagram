package org.reactome.web.diagram.util.svg.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.util.svg.events.SVGThumbnailAreaMovedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SVGThumbnailAreaMovedHandler extends EventHandler {

    void onSVGThumbnailAreaMoved(SVGThumbnailAreaMovedEvent event);
}
