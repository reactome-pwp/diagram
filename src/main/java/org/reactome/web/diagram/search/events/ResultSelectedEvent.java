package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.ResultItem;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultSelectedEvent extends GwtEvent<ResultSelectedHandler> {
    public static Type<ResultSelectedHandler> TYPE = new Type<>();

    private ResultItem selectedResultItem;

    public ResultSelectedEvent(ResultItem selectedResultItem) {
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

    public ResultItem getSelectedResultItem() {
        return selectedResultItem;
    }

    @Override
    public String toString() {
        return "ResultSelectedEvent{" +
                ", selected=" + selectedResultItem.getStId() +
                '}';
    }
}
