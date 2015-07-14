package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.DatabaseObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DefinedSet extends EntitySet {

    public DefinedSet(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return DatabaseObjectImages.INSTANCE.definedSet();
    }
}
