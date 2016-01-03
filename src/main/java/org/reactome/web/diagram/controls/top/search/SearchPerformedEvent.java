package org.reactome.web.diagram.controls.top.search;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPerformedEvent extends GwtEvent<SearchPerformedHandler> {
    public static Type<SearchPerformedHandler> TYPE = new Type<>();

    private List<GraphObject> suggestions;

    public SearchPerformedEvent(List<GraphObject> suggestions) {
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

    public List<GraphObject> getSuggestions() {
        return suggestions;
    }
}
