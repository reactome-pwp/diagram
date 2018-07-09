package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.SuggestionSelectedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Deprecated
public interface SuggestionSelectedHandler extends EventHandler {
    void onSuggestionSelected(SuggestionSelectedEvent event);
}
