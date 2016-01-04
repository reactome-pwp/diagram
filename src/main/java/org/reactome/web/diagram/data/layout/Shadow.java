package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Shadow extends DiagramObject {

    NodeProperties getProp();

    List<Coordinate> getPoints();

    String getColour();

}
