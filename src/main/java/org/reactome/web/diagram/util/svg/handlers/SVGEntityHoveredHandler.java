package org.reactome.web.diagram.util.svg.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.util.svg.events.SVGEntityHoveredEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SVGEntityHoveredHandler extends EventHandler {
    void onSVGEntityHovered(SVGEntityHoveredEvent event);
}
