package org.reactome.web.diagram.search.results;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface ResultsWidget extends IsWidget {

    HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler);

    HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler);

    void updateResults(SearchArguments args, boolean clearSelection);

    void setFacets(List<FacetContainer> facets);

    void suspendSelection();

}
