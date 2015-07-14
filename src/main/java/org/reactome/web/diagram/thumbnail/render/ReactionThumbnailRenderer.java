package org.reactome.web.diagram.thumbnail.render;

import org.reactome.web.diagram.data.graph.model.PhysicalEntity;
import org.reactome.web.diagram.data.graph.model.ReactionLikeEvent;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ReactionThumbnailRenderer extends AbstractThumbnailRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Edge edge = (Edge) item;
        drawSegments(ctx, edge.getSegments(), factor, offset);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        try {
            Edge edge = (Edge) item;
            ReactionLikeEvent rle = edge.getDatabaseObject();
            if (rle == null) return; //It can only happen if the graph hasn't been loaded yet, so no hovering until then :(
            drawSegments(ctx, getSegments(item, rle.getInputs()), factor, offset);
            drawSegments(ctx, getSegments(item, rle.getOutputs()), factor, offset);
            drawSegments(ctx, getSegments(item, rle.getCatalysts()), factor, offset);
            drawSegments(ctx, getSegments(item, rle.getActivators()), factor, offset);
            drawSegments(ctx, getSegments(item, rle.getInhibitors()), factor, offset);
            drawSegments(ctx, getSegments(item, rle.getRequirements()), factor, offset);
        } catch (ClassCastException ex) {
            //Nothing here
        }

        draw(ctx, item, factor, offset);
    }

    private List<Segment> getSegments(DiagramObject item, List<PhysicalEntity> physicalEntities) {
        List<Segment> rtn = new LinkedList<Segment>();
        for (PhysicalEntity pe : physicalEntities) {
            for (DiagramObject obj : pe.getDiagramObjects()) {
                Node node = (Node) obj;

                for (Connector connector : node.getConnectors()) {
                    if (connector.getEdgeId().equals(item.getId())) {
                        rtn.addAll(connector.getSegments());
                    }
                }
            }
        }
        return rtn;
    }
}
