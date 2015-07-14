package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Compartment extends GO_CellularComponent {

    public Compartment(EntityNode node) {
        super(node);
    }

    @Override
    public ImageResource getImageResource() {
        return null;
    }
}
