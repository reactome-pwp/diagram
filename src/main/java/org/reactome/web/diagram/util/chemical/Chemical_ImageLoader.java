package org.reactome.web.diagram.util.chemical;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.reactome.web.diagram.events.StructureImageLoadedEvent;
import org.reactome.web.diagram.util.Console;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Chemical_ImageLoader {

    private static Chemical_ImageLoader loader;

    public static final Image LOADING = new Image(ChemicalImages.INSTANCE.loading());
    public static final Image NOT_FOUND = new Image(ChemicalImages.INSTANCE.notFound());

    public interface Handler {
        void onChemicalImageLoaded(Image image);
    }

    private EventBus eventBus;

    private Chemical_ImageLoader(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void initialise(EventBus eventBus){
        if (loader != null) {
            throw new RuntimeException("Chemical Image Loader has already been initialised. " +
                    "Only one initialisation is permitted per Diagram Viewer instance.");
        }
        loader = new Chemical_ImageLoader(eventBus);
    }

    public static Chemical_ImageLoader get() {
        if (loader == null) {
            throw new RuntimeException("Chemical Image Loader has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return loader;
    }

    public void loadImage(final Handler handler, String identifier){
        if(identifier.startsWith("CHEBI")){
            loadChEBI(handler, identifier);
        } else if(identifier.startsWith("CHEMBL")) {
            loadCHEMBL(handler, identifier);
        } else {
            Console.warn("Chemical images for " + identifier + " have not been contemplated");
            handler.onChemicalImageLoaded(NOT_FOUND);
            eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), this);
        }
    }

    public void loadCHEMBL(final Handler handler, String identifier){
        String id = identifier.replaceAll("^CHEMBL[-:_]?", "");
        String url = "http://www.ebi.ac.uk/chembl/api/data/image/" + id + "?format=png";
//        String url = "https://www.ebi.ac.uk/chembl/api/data/image/CHEMBL209793?format=png";

        //The following work forces a Access-Control-Allow-Origin header by CHEMBL because
        //it is only added when the header X-Requested-With:XMLHttpRequest is added
        //Ideally the Access-Control-Allow-Origin:* should be provided by default for every
        //image query, so the loading can be automatically performed by the IMG TAG itself and
        //then used by the canvas (with the permission for exporting) [See ChEBI or PDBe use case]
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("X-Requested-With", "XMLHttpRequest");
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    Image image = new Image("data:image/png;base64," + response.getText());
                    forceLoadImage(handler, image);
                }

                @Override
                public void onError(Request request, Throwable throwable) {
                    handler.onChemicalImageLoaded(NOT_FOUND);
                }
            });
        } catch (RequestException e) {
            handler.onChemicalImageLoaded(NOT_FOUND);
        }
    }

    public void loadChEBI(final Handler handler, String identifier){
        String id = identifier.replaceAll("^CHEBI[-:_]?", "");
        String url = "http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&chebiId=" + id + "&dimensions=200&transbg=true";

        final Image image = new Image(url);
        image.setAltText(url);
        //Next line is meant to avoid the "SecurityError" problem when exporting tainted canvases
        image.getElement().setAttribute("crossOrigin", "anonymous");
        forceLoadImage(handler, image);
    }

    private void forceLoadImage(final Handler handler, final Image image){
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent loadEvent) {
                //It was just added to the DOM to force load so this method is called
                image.getElement().removeFromParent();
                image.setVisible(true);
                handler.onChemicalImageLoaded(image);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(image), Chemical_ImageLoader.this);
            }
        });
        image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent errorEvent) {
                image.getElement().removeFromParent();
                handler.onChemicalImageLoaded(NOT_FOUND);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), Chemical_ImageLoader.this);
            }
        });
        //Making it invisible and attaching it to the DOM forces the loading of the image (so the previous handler is called)
        image.setVisible(false);
        RootPanel.get().add(image);
    }


    static {
        RootPanel.get().add(LOADING);
        LOADING.setAltText("Chemical protein structure");
        LOADING.getElement().removeFromParent();
        LOADING.getElement().setAttribute("crossOrigin", "anonymous");

        RootPanel.get().add(NOT_FOUND);
        NOT_FOUND.setAltText("Chemical structure not found");
        NOT_FOUND.getElement().removeFromParent();
        NOT_FOUND.getElement().setAttribute("crossOrigin", "anonymous");
    }

    interface ChemicalImages extends ClientBundle {

        ChemicalImages INSTANCE = GWT.create(ChemicalImages.class);

        @Source("loading.png")
        ImageResource loading();

        @Source("notFound.png")
        ImageResource notFound();
    }
}
