package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionSelectedEvent extends GwtEvent<SuggestionSelectedHandler> {
    public static Type<SuggestionSelectedHandler> TYPE = new Type<SuggestionSelectedHandler>();

    private DatabaseObject databaseObject;

    public SuggestionSelectedEvent(DatabaseObject databaseObject) {
        this.databaseObject = databaseObject;
    }

    @Override
    public Type<SuggestionSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SuggestionSelectedHandler handler) {
        handler.onSuggestionSelected(this);
    }

    public DatabaseObject getDatabaseObject() {
        return databaseObject;
    }

    @Override
    public String toString() {
        return "SuggestionSelectedEvent{" +
                ", selected=" + databaseObject +
                '}';
    }
}
