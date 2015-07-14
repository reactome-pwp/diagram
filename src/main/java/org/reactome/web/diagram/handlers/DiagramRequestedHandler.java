package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramRequestedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramRequestedHandler extends EventHandler {

    void onDiagramRequested(DiagramRequestedEvent event);

}
