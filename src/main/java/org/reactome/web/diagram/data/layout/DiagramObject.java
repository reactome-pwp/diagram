package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramObject extends QuadTreeBox {

    Long getId(); //unique per diagram id 643809

    Long getReactomeId();

    String getDisplayName();

    String getSchemaClass();

    String getRenderableClass();

    Coordinate getPosition();

    Boolean getIsDisease();

    Boolean getIsFadeOut();

    <T extends GraphObject> T getGraphObject();

    void setGraphObject(GraphObject obj);

    void setSchemaClass(String schemaClass);

    //The implementation of this is done in DiagramObjectCategory
    //(http://www.gwtproject.org/doc/latest/DevGuideAutoBeans.html#categories)
    boolean isHovered(Coordinate coordinate);

}
