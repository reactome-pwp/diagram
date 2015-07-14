package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.events.DatabaseObjectHoveredEvent;
import org.reactome.web.diagram.events.DatabaseObjectSelectedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
abstract class InfoActionsHelper {

    static ClickHandler getLinkClickHandler(final DatabaseObject reaction, final EventBus eventBus, final Object source){
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.fireEventFromSource(new DatabaseObjectSelectedEvent(reaction, true), source);
            }
        };
    }

    static MouseOutHandler getLinkMouseOut(final EventBus eventBus, final Object source){
        return new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                eventBus.fireEventFromSource(new DatabaseObjectHoveredEvent(), source);
            }
        };
    }

    static MouseOverHandler getLinkMouseOver(final DatabaseObject reaction, final EventBus eventBus, final Object source){
        return new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                eventBus.fireEventFromSource(new DatabaseObjectHoveredEvent(reaction), source);
            }
        };
    }
}
