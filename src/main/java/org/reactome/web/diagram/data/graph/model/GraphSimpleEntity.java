package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.util.chemical.ChEBI_ImageLoader;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphSimpleEntity extends GraphPhysicalEntity implements ChEBI_ImageLoader.Handler {

    private ImageElement chemicalImage;

    public GraphSimpleEntity(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.simpleEntity();
    }

    public ImageElement getChemicalImage() {
        if(chemicalImage == null){
            chemicalImage = ImageElement.as(ChEBI_ImageLoader.LOADING.getElement());
            ChEBI_ImageLoader.get().loadImage(this, getIdentifier());
        }
        return chemicalImage;
    }

    @Override
    public void onChemicalImageLoaded(Image image) {
        chemicalImage = ImageElement.as(image.getElement());
    }
}
