package org.reactome.web.diagram.util.chemical;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventBus;
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
        final String url;
        if(identifier.startsWith("CHEBI")){
            String id = identifier.replaceAll("^CHEBI[-:_]?", "");
            url = "http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&chebiId=" + id + "&dimensions=200&transbg=true";
        } else if(identifier.startsWith("CHEMBL")) {
            String id = identifier.replaceAll("^CHEMBL[-:_]?", "");
//            url = "http://www.ebi.ac.uk/chembl/compound/displayimage_large/" + id;

//            url = "https://www.ebi.ac.uk/chembl/api/data/image/CHEMBL209793?format=svg";
            url = "http://www.ebi.ac.uk/chembl/api/data/image/" + id + "?format=svg";
        } else {
            Console.warn("Chemical images for " + identifier + " have not been contemplated");
            handler.onChemicalImageLoaded(NOT_FOUND);
            eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), this);
            return;
        }

        final Image rtn = new Image(url);
        rtn.setAltText(url);
        //Next line is meant to avoid the "SecurityError" problem when exporting tainted canvases
        rtn.getElement().setAttribute("crossOrigin", "anonymous");
        rtn.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent loadEvent) {
                //It was just added to the DOM to force load so this method is called
                rtn.getElement().removeFromParent();
                rtn.setVisible(true);
                handler.onChemicalImageLoaded(rtn);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(rtn), Chemical_ImageLoader.this);
            }
        });
        rtn.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent errorEvent) {
                rtn.getElement().removeFromParent();
                handler.onChemicalImageLoaded(NOT_FOUND);
                eventBus.fireEventFromSource(new StructureImageLoadedEvent(NOT_FOUND), Chemical_ImageLoader.this);
            }
        });
        //Making it invisible and attaching it to the DOM forces the loading of the image (so the previous handler is called)
        rtn.setVisible(false);
        RootPanel.get().add(rtn);
    }

    public static final Image LOADING = new Image(ChemicalImages.INSTANCE.loading());
    public static final Image NOT_FOUND = new Image(ChemicalImages.INSTANCE.notFound());

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
