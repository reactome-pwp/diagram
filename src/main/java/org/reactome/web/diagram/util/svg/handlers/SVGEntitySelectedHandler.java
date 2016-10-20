package org.reactome.web.diagram.util.svg.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.util.svg.events.SVGEntitySelectedEvent;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SVGEntitySelectedHandler extends EventHandler {

    void onSVGEntitySelected(SVGEntitySelectedEvent event);

}
