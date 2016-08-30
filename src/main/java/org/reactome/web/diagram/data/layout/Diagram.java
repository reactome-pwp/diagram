package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * This relates to the root element of JSON (Name inherited from the original XML)
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Diagram {

    /**
     * Indicates whether the pathways is related to a disease (If false it is a normal pathway)
     */
    Boolean getIsDisease();

    /**
     * Indicates whether the pathway has to be treated as a normal drawing even though it represents a disease
     */
    Boolean getForNormalDraw();

    /**
     * Returns the name of the pathway represented with the diagram (process)
     */
    String getDisplayName();

    /**
     * The list of contained nodes
     */
    List<Node> getNodes();

    /**
     * The list of contained notes
     */
    List<Note> getNotes();

    /**
     * The list of contained edges
     */
    List<Edge> getEdges();

    /**
     * The list of contained links
     */
    List<Link> getLinks();

    /**
     * The list of contained compartments
     */
    List<Compartment> getCompartments();

    /**
     * The list of contained shadows
     */
    List<Shadow> getShadows();

    /**
     * Related pathway DB_ID
     */
    Long getDbId();

    /**
     * Related pathway ST_ID
     */
    String getStableId();

    /**
     * Universal Min Max of X and Y for all diagram objects
     */
    Integer getMinX();
    Integer getMaxX();
    Integer getMinY();
    Integer getMaxY();

    /**
     * Related clickable picture in svg format
     */
    String getPicture();
}
