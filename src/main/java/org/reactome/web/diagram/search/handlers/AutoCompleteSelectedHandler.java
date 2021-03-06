package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.AutoCompleteSelectedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface AutoCompleteSelectedHandler extends EventHandler {
    void onAutoCompleteSelected(AutoCompleteSelectedEvent event);
}
