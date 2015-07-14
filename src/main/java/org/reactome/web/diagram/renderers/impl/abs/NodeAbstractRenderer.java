package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.SegmentCategory;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.RendererManager;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class NodeAbstractRenderer extends AbstractRenderer {
    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        if(item.getDisplayName() == null || item.getDisplayName().isEmpty()) { return; }
        TextMetrics metrics = ctx.measureText(item.getDisplayName());

        Node node = (Node) item;
        Coordinate textPos = node.getPosition().transform(factor, offset);
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        if(metrics.getWidth()<=prop.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), textPos);
        }else{
            textRenderer.drawTextMultiLine(ctx, item, factor, offset);
        }
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public Long getHovered(DiagramObject item, Coordinate pos) {
        if (isVisible(item)) {
            Node node = (Node) item;
            if (node.isHovered(pos)) {
                return node.getId();
            }

            for (Connector connector : node.getConnectors()) {
                if (RendererManager.get().getConnectorRenderer().stoichiometryVisible()) {
                    Stoichiometry stoichiometry = connector.getStoichiometry();
                    if (stoichiometry != null && stoichiometry.getValue() != null && stoichiometry.getValue() > 1) {
                        if (ShapeCategory.isHovered(stoichiometry.getShape(), pos)) {
                            return connector.getEdgeId();
                        }
                    }
                }

                Shape shape = connector.getEndShape();
                if (shape != null) {
                    if (ShapeCategory.isHovered(shape, pos)) {
                        return connector.getEdgeId();
                    }
                }

                for (Segment segment : connector.getSegments()) {
                    if (SegmentCategory.isInSegment(segment, pos)) {
                        return connector.getEdgeId();
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Double getExpressionHovered(DiagramObject item, Coordinate pos, int t) {
        if(item .getDatabaseObject()!=null) {
            List<Double> expression = item.getDatabaseObject().getExpression();
            if (expression != null){
                return expression.get(t);
            }
        }
        return null;
    }

    public void drawCross(AdvancedContext2d ctx, Node node, NodeProperties prop){
        if(node.getIsCrossed()!=null){
            ctx.save();
            ctx.beginPath();
            ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getDisease());
            ctx.moveTo(prop.getX(), prop.getY());
            ctx.lineTo(prop.getX() + prop.getWidth(), prop.getY() + prop.getHeight());
            ctx.moveTo(prop.getX(), prop.getY() + prop.getHeight());
            ctx.lineTo(prop.getX() + prop.getWidth(), prop.getY());
            ctx.stroke();
            ctx.restore();
        }
    }

    public abstract void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed);
}
