package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.Renderer;
import org.reactome.web.diagram.renderers.RendererManager;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class AbstractRenderer implements Renderer {

    @Override
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        return null;
    }

    @Override
    public void drawEnrichment(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, Double factor, Coordinate offset){
        draw(ctx, item, factor, offset); //By default the normal draw method is called
    }

    @Override
    public void drawExpression(AdvancedContext2d ctx, OverlayContext overlay, DiagramObject item, int t, double min, double max, Double factor, Coordinate offset){
        GraphObject graphObject = item.getGraphObject();
        List<Double> expression = graphObject.getExpression();
        double value = min;
        if(expression!=null) {
            value = expression.get(t);
        }
        ctx.setFillStyle(AnalysisColours.get().expressionGradient.getColor(value, min, max));
        draw(ctx, item, factor, offset); //By default the normal draw method is called
    }

    public void drawAttachments(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset, boolean fill){
        List<NodeAttachment> atList = node.getNodeAttachments();
        if(atList!=null){
            for (NodeAttachment nodeAttachment : atList) {
                Shape s = ShapeFactory.transform(nodeAttachment.getShape(), factor, offset);
                ctx.beginPath();
                ctx.rect(
                        s.getA().getX(),
                        s.getA().getY(),
                        s.getB().getX()-s.getA().getX(),
                        s.getB().getY()-s.getA().getY()
                );
                ctx.stroke();
                if (fill) {
                    ctx.fill();
                } else {
                    ctx.clearRect(
                            s.getA().getX(),
                            s.getA().getY(),
                            s.getB().getX() - s.getA().getX(),
                            s.getB().getY() - s.getA().getY()
                    );
                }

                if(nodeAttachment.getLabel()!=null) {
                    //TODO move this to a higher level and set it once OR use the TextRenderer to draw it
                    // for setting the text of the attachment
                    ctx.save();
                    ctx.setTextAlign(Context2d.TextAlign.CENTER);
                    ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
                    ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
                    ctx.setFillStyle(DiagramColours.get().PROFILE.getAttachment().getText());
                    Coordinate c = s.getB().minus(s.getA()).divide(2).add(s.getA());
                    ctx.fillText(nodeAttachment.getLabel(), c.getX(), c.getY());
                    ctx.restore();
                }
            }
        }
    }

    public void drawSegments(AdvancedContext2d ctx, List<Segment> segments, Double factor, Coordinate offset) {
        for(Segment segment : segments) {
            segment = SegmentFactory.transform(segment, factor, offset);
            ctx.beginPath();
            ctx.moveTo(segment.getFrom().getX(), segment.getFrom().getY());
            ctx.lineTo(segment.getTo().getX(), segment.getTo().getY());
            ctx.stroke();
        }
    }

    public void drawSegments(AdvancedContext2d ctx,
                             DiagramObject item,
                             List<GraphPhysicalEntity> physicalEntities,
                             Double factor,
                             Coordinate offset) {
        boolean stoichiometryVisible = RendererManager.get().getConnectorRenderer().stoichiometryVisible();
        for(GraphPhysicalEntity pe : physicalEntities) {
            if(!isVisible(pe)) continue;
            for(DiagramObject obj : pe.getDiagramObjects()) {
                Node node = (Node) obj;
                for(Connector connector : node.getConnectors()) {
                    if(connector.getEdgeId().equals(item.getId())) {
                        drawSegments(ctx, connector.getSegments(), factor, offset);
                        ShapeAbstractRenderer.draw(ctx, connector.getEndShape(), factor, offset);
                        if(stoichiometryVisible) {
                            Stoichiometry stoichiometry = connector.getStoichiometry();
                            if(stoichiometry != null && stoichiometry.getValue() > 1) {
                                ShapeAbstractRenderer.draw(ctx, stoichiometry.getShape(), factor, offset);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isVisible(GraphPhysicalEntity pe) {
        for (DiagramObject diagramObject : pe.getDiagramObjects()) {
            Renderer renderer = RendererManager.get().getRenderer(diagramObject);
            if (renderer == null || !renderer.isVisible(diagramObject)) {
                return false;
            }
        }
        return true;
    }
}
