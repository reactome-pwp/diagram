package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface NodeCommon extends DiagramObject {

    NodeProperties getProp();

    Identifier getIdentifier();

    Coordinate getTextPosition();

    Bound getInsets();

    Color getBgColor();

    Color getFgColor();

    Boolean getIsCrossed();

    Boolean getNeedDashedBorder();

    List<SummaryItem> getSummaryItems();

}
