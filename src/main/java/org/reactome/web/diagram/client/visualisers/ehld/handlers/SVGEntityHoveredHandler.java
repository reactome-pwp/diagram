package org.reactome.web.diagram.client.visualisers.ehld.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGEntityHoveredEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public interface SVGEntityHoveredHandler extends EventHandler {
    void onSVGEntityHovered(SVGEntityHoveredEvent event);
}
