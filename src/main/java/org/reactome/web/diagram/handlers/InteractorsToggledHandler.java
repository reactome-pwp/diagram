package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsToggledEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface InteractorsToggledHandler extends EventHandler {
    void onInteractorsToggled(InteractorsToggledEvent event);
}
