package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Deprecated
public class SuggestionSelectedEvent extends GwtEvent<SuggestionSelectedHandler> {
    public static Type<SuggestionSelectedHandler> TYPE = new Type<>();

    private SearchResultObject searchResultObject;

    public SuggestionSelectedEvent(SearchResultObject searchResultObject) {
        this.searchResultObject = searchResultObject;
    }

    @Override
    public Type<SuggestionSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SuggestionSelectedHandler handler) {
        handler.onSuggestionSelected(this);
    }

    public SearchResultObject getSearchResultObject() {
        return searchResultObject;
    }

    @Override
    public String toString() {
        return "SuggestionSelectedEvent{" +
                ", selected=" + searchResultObject +
                '}';
    }
}
