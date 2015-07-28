package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionSelectedEvent extends GwtEvent<SuggestionSelectedHandler> {
    public static Type<SuggestionSelectedHandler> TYPE = new Type<>();

    private GraphObject graphObject;

    public SuggestionSelectedEvent(GraphObject graphObject) {
        this.graphObject = graphObject;
    }

    @Override
    public Type<SuggestionSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SuggestionSelectedHandler handler) {
        handler.onSuggestionSelected(this);
    }

    public GraphObject getGraphObject() {
        return graphObject;
    }

    @Override
    public String toString() {
        return "SuggestionSelectedEvent{" +
                ", selected=" + graphObject +
                '}';
    }
}
