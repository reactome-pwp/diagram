package org.reactome.web.diagram.search.results.local;

import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.UriUtils;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.search.results.data.DiagramSearchException;
import org.reactome.web.diagram.search.results.data.DiagramSearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.diagram.util.Console;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class InDiagramOccurrencesFactory {

    private static final String URL = DiagramFactory.SERVER + "/ContentService/search/diagram/##DIAGRAM##/occurrences/##INSTANCE##";
    private static Request request;

    public interface Handler {
        void onOccurrencesSuccess(Occurrences occurrences);
        void onOccurrencesError(String msg);
    }

    public static void searchForInstanceInDiagram(final String instance, final String diagram, final Handler handler) {
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = URL.replace("##DIAGRAM##", UriUtils.encode(diagram))
                        .replace("##INSTANCE##", UriUtils.encode(instance));

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onOccurrencesSuccess(getOccurrences(response.getText(), handler));
                            break;
                        default:
                            handler.onOccurrencesError(response.getStatusText());
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    Console.error(exception.getCause());
                    handler.onOccurrencesError(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onOccurrencesError(ex.getMessage());
        }
    }

    private static Occurrences getOccurrences(final String json, final Handler handler) {
        Occurrences rtn = null;
        try {
            rtn = DiagramSearchResultFactory.getSearchObject(Occurrences.class, json);
        } catch (DiagramSearchException ex) {
            handler.onOccurrencesError(ex.getMessage());
        }
        return rtn;
    }
}
