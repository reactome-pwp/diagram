package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramInternalErrorHandler extends EventHandler {
    void onDiagramInternalError(DiagramInternalErrorEvent event);
}
