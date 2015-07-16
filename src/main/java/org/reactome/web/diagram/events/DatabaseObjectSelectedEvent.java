package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.handlers.DatabaseObjectSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DatabaseObjectSelectedEvent extends GwtEvent<DatabaseObjectSelectedHandler> {
    public static Type<DatabaseObjectSelectedHandler> TYPE = new Type<>();

    private DatabaseObject databaseObject;
    private boolean zoom;
    private boolean fireExternally;

    public DatabaseObjectSelectedEvent(DatabaseObject databaseObject, boolean zoom) {
        this(databaseObject, zoom, true);
    }

    public DatabaseObjectSelectedEvent(DatabaseObject databaseObject, boolean zoom, boolean fireExternally) {
        this.databaseObject = databaseObject;
        this.zoom = zoom;
        this.fireExternally = fireExternally;
    }

    @Override
    public Type<DatabaseObjectSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DatabaseObjectSelectedHandler handler) {
        handler.onDatabaseObjectSelected(this);
    }

    public DatabaseObject getDatabaseObject() {
        return databaseObject;
    }

    public boolean getZoom() {
        return zoom;
    }

    public boolean getFireExternally() {
        return fireExternally;
    }

    @Override
    public String toString() {
        return "DatabaseObjectSelectedEvent{" +
                "databaseObject=" + databaseObject +
                '}';
    }
}
