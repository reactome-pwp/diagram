package org.reactome.web.diagram.launcher.search;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPerformedEvent extends GwtEvent<SearchPerformedHandler> {
    public static Type<SearchPerformedHandler> TYPE = new Type<SearchPerformedHandler>();

    private List<DatabaseObject> suggestions;

    public SearchPerformedEvent(List<DatabaseObject> suggestions) {
        this.suggestions = suggestions;
    }

    @Override
    public Type<SearchPerformedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchPerformedHandler handler) {
        handler.onSearchPerformed(this);
    }

    public List<DatabaseObject> getSuggestions() {
        return suggestions;
    }
}
