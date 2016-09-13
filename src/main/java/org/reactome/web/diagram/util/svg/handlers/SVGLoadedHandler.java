package org.reactome.web.diagram.util.svg.handlers;


import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.util.svg.events.SVGLoadedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SVGLoadedHandler extends EventHandler {

    void onSVGLoaded(SVGLoadedEvent event);

}
