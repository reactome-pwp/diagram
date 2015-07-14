package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.handlers.DatabaseObjectHoveredHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DatabaseObjectHoveredEvent extends GwtEvent<DatabaseObjectHoveredHandler> {
    public static Type<DatabaseObjectHoveredHandler> TYPE = new Type<DatabaseObjectHoveredHandler>();

    private DatabaseObject databaseObject;

    public DatabaseObjectHoveredEvent(){
        this.databaseObject = null;
    }

    public DatabaseObjectHoveredEvent(DatabaseObject databaseObject) {
        this.databaseObject = databaseObject;
    }

    @Override
    public Type<DatabaseObjectHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DatabaseObjectHoveredHandler handler) {
        handler.onDatabaseObjectHovered(this);
    }

    public DatabaseObject getDatabaseObject() {
        return databaseObject;
    }

    public List<DiagramObject> getHoveredObjects() {
        return databaseObject != null ? databaseObject.getDiagramObjects() : new LinkedList<DiagramObject>();
    }

    @Override
    public String toString() {
        return "DatabaseObjectHoveredEvent{" +
                "databaseObject=" + databaseObject +
                '}';
    }
}
