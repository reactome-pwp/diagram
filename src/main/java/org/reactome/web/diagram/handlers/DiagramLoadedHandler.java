package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramLoadedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramLoadedHandler extends EventHandler {

    void onDiagramLoaded(DiagramLoadedEvent event);
}
