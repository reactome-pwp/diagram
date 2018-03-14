package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.content.Content;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class FlaggedElementsLoader implements RequestCallback {

    public interface Handler {
        void flaggedElementsLoaded(String term, Collection<String> toFlag, boolean notify);

        void onFlaggedElementsLoaderError(Throwable exception);
    }

    static final String PREFIX = DiagramFactory.SERVER + "/ContentService/search/diagram/##pathway##/flag?query=##term##";

    private String term;
    private Boolean notify;

    Handler handler;
    Request request;

    public FlaggedElementsLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel() {
        if (request != null && request.isPending()) request.cancel();
        this.term = null;
        this.notify = null;
    }

    public void load(Content content, String term, boolean notify) {
        // Any previous request has to be canceled
        cancel();

        if (content == null) return;

        this.term = term;
        this.notify = notify;

        String url = PREFIX.replace("##pathway##", content.getStableId()).replace("##term##", term);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onFlaggedElementsLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        Collection<String> toFlag = new HashSet<>();
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                JSONArray list = JSONParser.parseStrict(response.getText()).isArray();
                for (int i = 0; i < list.size(); ++i) {
                    toFlag.add(list.get(i).isString().stringValue());
                }
                handler.flaggedElementsLoaded(term, toFlag, notify);
                break;
            case Response.SC_NOT_FOUND:
                handler.flaggedElementsLoaded(term, new HashSet(), notify);
                break;
            default:
                //TODO: Propagate the error from the response from the returned JSON instead?
                handler.onFlaggedElementsLoaderError(new Exception(response.getStatusText()));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        handler.onFlaggedElementsLoaderError(exception);
    }
}
