package org.reactome.web.diagram.search.results;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface ResultsWidget extends IsWidget {

    void updateResults(SearchArguments args);

    HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler);

}
