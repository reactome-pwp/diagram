package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GO_CellularComponent extends DatabaseObject {

    private List<DatabaseObject> containedElements = new ArrayList<DatabaseObject>();

    public GO_CellularComponent(EntityNode node) {
        super(node);
    }

    public boolean addContainedElement(DatabaseObject databaseObject){
        return this.containedElements.add(databaseObject);
    }

    public List<DatabaseObject> getContainedElements() {
        return containedElements;
    }

    @Override
    public ImageResource getImageResource() {
        return null;
    }
}
