package org.reactome.web.diagram.data.layout.category;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeCommon;
import org.reactome.web.diagram.data.layout.NodeProperties;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class NodeCommonCategory {
    public static boolean isHovered(NodeCommon node, Coordinate coordinate) {
        NodeProperties prop = node.getProp();
        return (
                (coordinate.getX() >= prop.getX()) &&
                (coordinate.getX() <= (prop.getX() + prop.getWidth()))) &&
                ((coordinate.getY() >= prop.getY()) &&
                (coordinate.getY() <= (prop.getY() + prop.getHeight()))
        );
    }
}
