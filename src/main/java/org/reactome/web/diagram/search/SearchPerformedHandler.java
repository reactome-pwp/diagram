package org.reactome.web.diagram.search;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface SearchPerformedHandler extends EventHandler {
    void onSearchPerformed(SearchPerformedEvent event);
}
