package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.factory.GraphFactory;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphLoader implements RequestCallback {

    public interface Handler {
        void graphLoaded(Graph graph, long time);
        void onGraphLoaderError(Throwable exception);
    }

    private final static String PREFIX = "/download/current/diagram/";

    private Handler handler;
    private Request request;

    GraphLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.request.cancel();
        }
    }

    public void load(String stId){
        String url = PREFIX + stId + ".graph.json";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onGraphLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                try {
                    long start = System.currentTimeMillis();
                    //Creates the graph
                    Graph graph = GraphFactory.getGraphObject(Graph.class, response.getText());
                    long time = System.currentTimeMillis() - start;
                    this.handler.graphLoaded(graph, time);
                } catch (DiagramObjectException e) {
                    this.handler.onGraphLoaderError(e);
                }
                break;
            default:
                this.handler.onGraphLoaderError(new Exception(response.getStatusText()));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onGraphLoaderError(exception);
    }
}
