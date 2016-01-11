package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.ProcessNodeAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ProcessNodeRenderer000 extends ProcessNodeAbstractRenderer {
    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //No text at this level
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH + 4);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.beginPath();
        ctx.rect(
                prop.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getWidth() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2,
                prop.getHeight() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2
        );
    }

    @Override
    public void drawAnalysisResult(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        if(!isVisible(item)) return;

        Node node = (Node) item;
        GraphPathway graphPathway = node.getGraphObject();
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double x = prop.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH;
        double y = prop.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH;
        double w = prop.getWidth() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2;
        double h = prop.getHeight() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2;
        double l = w * graphPathway.getPercentage();

        ctx.save();
        setColourProperties(ctx, ColourProfileType.NORMAL);
        ctx.setStrokeStyle(ctx.getFillStyle());
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH);
        ctx.setFillStyle("#FEFDFF");
        ctx.beginPath();
        ctx.rect(x, y, w, h);
        ctx.stroke();
        ctx.fill();
        ctx.restore();

        ctx.save();
        ctx.setStrokeStyle(ctx.getFillStyle());
        ctx.setLineWidth(RendererProperties.PROCESS_NODE_INSET_WIDTH);
        ctx.beginPath();
        ctx.moveTo(x + l, y);
        ctx.lineTo(x, y);
        ctx.lineTo(x, y + h);
        ctx.lineTo(x + l, y + h);
        if(graphPathway.getPercentage()>0.99){
            ctx.closePath();
        }
        ctx.stroke();
        ctx.restore();

        ctx.save();
        ctx.setFillStyle("#FEFDFF");
        ctx.beginPath();
        ctx.fillRect(x, y, w, h);
        ctx.restore();
    }
}
