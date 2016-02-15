package org.reactome.web.diagram.data.loader;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsFactory;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.InteractorsErrorEvent;

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
        if(request!=null && request.isPending()){
            request.cancel();
        }
    }

    public void load(DiagramContent content, String resource){
        // Any previous request has to be canceled
        cancel();

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
                fireDeferredErrorEvent(resource, e.getMessage(), InteractorsErrorEvent.Level.ERROR_RECOVERABLE);
            }
        } else {
            fireDeferredErrorEvent(resource, "No target entities for interactors", InteractorsErrorEvent.Level.WARNING);
        }
    }

    private void fireDeferredErrorEvent(final String resource, final String message, final InteractorsErrorEvent.Level level){
        // Firing of the error event is deferred to ensure that InteractorsResourceChanged
        // event is handled first by the rest of the modules.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                handler.onInteractorsLoaderError(new InteractorsException(resource, message, level));
            }
        });
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
                this.handler.onInteractorsLoaderError(new InteractorsException(resource, response.getStatusText(), InteractorsErrorEvent.Level.ERROR_RECOVERABLE));
        }

    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onInteractorsLoaderError(new InteractorsException(resource, exception.getMessage()));
    }
}
