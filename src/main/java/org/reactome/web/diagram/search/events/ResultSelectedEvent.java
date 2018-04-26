package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultSelectedEvent extends GwtEvent<ResultSelectedHandler> {
    public static Type<ResultSelectedHandler> TYPE = new Type<>();

    private SearchResultObject selectedResultItem;

    public ResultSelectedEvent(SearchResultObject selectedResultItem) {
        this.selectedResultItem = selectedResultItem;
    }

    @Override
    public Type<ResultSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ResultSelectedHandler handler) {
        handler.onResultSelected(this);
    }

    public SearchResultObject getSelectedResultItem() {
        return selectedResultItem;
    }

    @Override
    public String toString() {
        return "ResultSelectedEvent{" +
                ", selected=" + selectedResultItem.getPrimarySearchDisplay() +
                '}';
    }
}
