package org.reactome.web.diagram.util.chemical;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class Chemical_ImageLoader {

    public interface Handler {
        void onChemicalImageLoaded(Image image);
    }

    public static void initialise(EventBus eventBus){
        ChEBI_ImageLoader.initialise(eventBus);
        ChEMBL_ImageLoader.initialise(eventBus);
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
