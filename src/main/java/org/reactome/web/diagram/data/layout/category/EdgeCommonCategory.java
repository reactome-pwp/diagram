package org.reactome.web.diagram.data.layout.category;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.EdgeCommon;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.Shape;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class EdgeCommonCategory {

    public static boolean isHovered(EdgeCommon edge, Coordinate coordinate) {
        Shape shape = edge.getReactionShape();
        if(shape!=null){
            if(ShapeCategory.isHovered(shape, coordinate)){
                return true;
            }
        }

        shape = edge.getEndShape();
        if(shape!=null){
            if(ShapeCategory.isHovered(shape, coordinate)){
                return true;
            }
        }

        for (Segment segment : edge.getSegments()) {
            if (SegmentCategory.isInSegment(segment, coordinate)) {
                return true;
            }
        }

        return false;
    }
}
