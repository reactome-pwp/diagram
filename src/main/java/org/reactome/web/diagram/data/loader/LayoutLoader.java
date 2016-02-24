package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectsFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LayoutLoader implements RequestCallback {

    public interface Handler {
        void layoutLoaded(Diagram diagram, long time);
        void onLayoutLoaderError(Throwable exception);
    }

    public static String PREFIX = DiagramFactory.SERVER + "/download/current/diagram/";
    public static String SUFFIX = ".json?v=" + LoaderManager.version;


    private Handler handler;
    private Request request;

    LayoutLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.request.cancel();
        }
    }

    void load(String stId){
        String url = PREFIX + stId + SUFFIX;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onLayoutLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                try {
                    long start = System.currentTimeMillis();
                    //Creates the rawmodel
                    Diagram diagram = DiagramObjectsFactory.getModelObject(Diagram.class, response.getText());
                    long time = System.currentTimeMillis() - start;
                    this.handler.layoutLoaded(diagram, time);
                } catch (DiagramObjectException e) {
                    this.handler.onLayoutLoaderError(e);
                }
                break;
            default:
                this.handler.onLayoutLoaderError(new Exception(response.getStatusText()));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onLayoutLoaderError(exception);
    }
}
