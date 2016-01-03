package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.search.events.SuggestionSelectedEvent;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SelectionInfoPanel extends AbstractAccordionPanel implements SuggestionSelectedHandler {
    private EventBus eventBus;

    public SelectionInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onSuggestionSelected(SuggestionSelectedEvent event) {
        this.clear();

        GraphObject obj = event.getGraphObject();
        if(obj!=null) {
            this.add(new InfoPanel(eventBus, obj));
        }
    }
}
