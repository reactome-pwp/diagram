package org.reactome.web.diagram.client.visualisers.ehld.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGEntitySelectedEvent;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public interface SVGEntitySelectedHandler extends EventHandler {

    void onSVGEntitySelected(SVGEntitySelectedEvent event);

}
