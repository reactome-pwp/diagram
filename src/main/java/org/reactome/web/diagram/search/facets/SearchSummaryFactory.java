package org.reactome.web.diagram.search.facets;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.results.data.DiagramSearchException;
import org.reactome.web.diagram.search.results.data.DiagramSearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.SearchSummary;
import org.reactome.web.diagram.util.Console;

/**
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SearchSummaryFactory {
    private static final String URL = DiagramFactory.SERVER + "/ContentService/search/diagram/summary?##QUERY##&species=##SPECIES##&diagram=##DIAGRAM##";
    private static Request request;

    public interface Handler {
        void onSearchSummaryReceived(SearchSummary summary);
        void onSearchSummaryError(String msg);
    }

    public static void queryForSummary(final SearchArguments arguments, final Handler handler){
//        handler.onSearchSummaryReceived(getMockFacets());
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = URL.replace("##QUERY##", arguments.getQuery())
                        .replace("##SPECIES##", arguments.getSpecies())
                        .replace("##DIAGRAM##", arguments.getDiagramStId());

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onSearchSummaryReceived(getSummary(response.getText(), handler));
                            break;
                        default:
                            handler.onSearchSummaryError(response.getStatusText());
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    Console.error(exception.getCause());
                    handler.onSearchSummaryError(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
    }

    private static SearchSummary getSummary(final String json, final Handler handler) {
        SearchSummary rtn = null;
        try {
            rtn = DiagramSearchResultFactory.getSearchObject(SearchSummary.class, json);
        } catch (DiagramSearchException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
        return rtn;
    }

//    private static List<String> getMockFacets() {
//        Console.info("FacetsFactory: Updating with fake facets ");
//        return Arrays.asList("Protein", "Complex", "Set", "genes and transcripts", "Chemical", "Drug", "Blah");
//    }
}
