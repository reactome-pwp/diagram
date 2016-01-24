package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsFactory;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLoader implements RequestCallback {

    public interface Handler {
        void interactorsLoaded(RawInteractors interactors, long time);
        void onInteractorsLoaderError(InteractorsException exception);
    }

    final static String PREFIX = DiagramFactory.SERVER + "/ContentService/interactors/";

    Handler handler;
    Request request;

    String resource;

    public InteractorsLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.request.cancel();
        }
    }

    public void load(DiagramContent content, String resource){
        if(resource==null){
            this.handler.onInteractorsLoaderError(new InteractorsException(null, "Resource not specified"));
            return;
        }
        this.resource = resource;

        StringBuilder post = new StringBuilder();
        for (DiagramObject diagramObject : content.getDiagramObjects()) {
            GraphObject graphObject = diagramObject.getGraphObject();
            if(graphObject instanceof GraphPhysicalEntity){
                GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                if (pe.getIdentifier() != null) {
                    post.append(pe.getIdentifier()).append(",");
                }
            }
        }
        if(post.length()>0){
            post.delete(post.length()-1, post.length());
            String url;
            if (resource.toLowerCase().equals("static")) {
                url = PREFIX + "static/proteins/details/?v=" + LoaderManager.version;
            } else {
                url = PREFIX + "psicquic/proteins/" + resource + "/details?v=" + LoaderManager.version;
            }

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            try {
                this.request = requestBuilder.sendRequest(post.toString(), this);
            } catch (RequestException e) {
                this.handler.onInteractorsLoaderError(new InteractorsException(resource, e.getMessage()));
            }
        } else {
            handler.onInteractorsLoaderError(new InteractorsException(resource, "No target entities for interactors"));
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                RawInteractors interactors;
                try {
                    interactors = InteractorsFactory.getInteractorObject(RawInteractors.class, response.getText());
                } catch (InteractorsException e) {
                    this.handler.onInteractorsLoaderError(e);
                    return;
                }
                long time = System.currentTimeMillis() - start;
                this.handler.interactorsLoaded(interactors, time);
                break;
            default:
                this.handler.onInteractorsLoaderError(new InteractorsException(resource, response.getStatusText()));
        }

    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onInteractorsLoaderError(new InteractorsException(resource, exception.getMessage()));
    }
}
