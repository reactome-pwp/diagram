package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.SuggestionResetEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public interface SuggestionResetHandler extends EventHandler {
    void onSuggestionReset(SuggestionResetEvent event);
}
