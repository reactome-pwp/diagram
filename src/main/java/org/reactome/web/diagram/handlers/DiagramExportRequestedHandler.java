package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramExportRequestedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramExportRequestedHandler extends EventHandler {

    void onDiagramExportRequested(DiagramExportRequestedEvent event);

}
