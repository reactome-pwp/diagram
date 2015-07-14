package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DatabaseObjectSelectedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DatabaseObjectSelectedHandler extends EventHandler {

    void onDatabaseObjectSelected(DatabaseObjectSelectedEvent event);
}
