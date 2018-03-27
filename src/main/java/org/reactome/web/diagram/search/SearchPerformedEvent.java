package org.reactome.web.diagram.search;

import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPerformedEvent extends GwtEvent<SearchPerformedHandler> {
    public static Type<SearchPerformedHandler> TYPE = new Type<>();

    private SearchArguments searchArguments;
    private List<SearchResultObject> suggestions; //TODO remove this in the clean up

    public SearchPerformedEvent(SearchArguments args) {
        this.searchArguments = args;
//        this.suggestions = suggestions; //TODO remove this in the clean up
    }

    @Override
    public Type<SearchPerformedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchPerformedHandler handler) {
        handler.onSearchPerformed(this);
    }

    public SearchArguments getSearchArguments() {
        return searchArguments;
    }

    //TODO remove this in the clean up
    public List<SearchResultObject> getSuggestions() {
        return suggestions;
    }
}
