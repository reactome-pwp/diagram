package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import uk.ac.ebi.pwp.structures.quadtree.interfaces.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramObject extends QuadTreeBox {

    Long getId(); //unique per diagram id 643809

    Long getReactomeId();

    String getStableId();

    String getDisplayName();

    String getSchemaClass();

    String getRenderableClass();

    Coordinate getPosition();

    Boolean getIsDisease();

//    Boolean getIsOverlaid();

    Boolean getIsFadeOut();

    <T extends DatabaseObject> T getDatabaseObject();

    void setDatabaseObject(DatabaseObject obj);

    void setSchemaClass(String schemaClass);

    //The implementation of this is done in RawObjectCategory
    //(http://www.gwtproject.org/doc/latest/DevGuideAutoBeans.html#categories)
    boolean isHovered(Coordinate coordinate);

}
