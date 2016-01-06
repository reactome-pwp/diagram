package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsStatusChangedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface InteractorsStatusChangedHandler extends EventHandler {
    void onInteractorsStatusChangedEvent(InteractorsStatusChangedEvent event);
}
