package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DatabaseObjectHoveredEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DatabaseObjectHoveredHandler extends EventHandler {

    void onDatabaseObjectHovered(DatabaseObjectHoveredEvent event);
}
