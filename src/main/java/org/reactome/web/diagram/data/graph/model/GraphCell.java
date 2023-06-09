package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphCell extends GraphPhysicalEntity {


    public GraphCell(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.cell();
    }

    @Override
    public Set<GraphPhysicalEntity> getParticipants(){
        return new HashSet<>(children);
    }
}
