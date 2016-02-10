package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramLoadRequestHandler extends EventHandler {
    void onDiagramLoadRequest(DiagramLoadRequestEvent event);
}
