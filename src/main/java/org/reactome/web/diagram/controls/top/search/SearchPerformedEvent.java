package org.reactome.web.diagram.controls.top.search;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.SearchResultObject;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPerformedEvent extends GwtEvent<SearchPerformedHandler> {
    public static Type<SearchPerformedHandler> TYPE = new Type<>();

    private String term;
    private List<SearchResultObject> suggestions;

    public SearchPerformedEvent(String term, List<SearchResultObject> suggestions) {
        this.term = term;
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

    public String getTerm() {
        return term;
    }

    public List<SearchResultObject> getSuggestions() {
        return suggestions;
    }
}
