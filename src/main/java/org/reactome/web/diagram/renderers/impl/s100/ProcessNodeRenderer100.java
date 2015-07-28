package org.reactome.web.diagram.renderers.impl.s100;

import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.impl.abs.ProcessNodeAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ProcessNodeRenderer100 extends ProcessNodeAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();

        ctx.save();
        ctx.setFillStyle("#FEFDFF");
        ctx.beginPath();
        ctx.rect(
                prop.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getWidth() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2,
                prop.getHeight() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2
        );
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.beginPath();
        ctx.rect(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                prop.getHeight()
        );
    }

    @Override
    public void drawAnalysisResult(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.save();
        setColourProperties(ctx, ColourProfileType.NORMAL);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();
        ctx.restore();

        GraphPathway pathway = node.getGraphObject();
        ctx.beginPath();
        ctx.fillRect(
                prop.getX(),
                prop.getY(),
                pathway.getPercentage() * prop.getWidth(),
                prop.getHeight()
        );

        ctx.save();
        ctx.setFillStyle("#FEFDFF");
        ctx.setGlobalAlpha(0.75);
        ctx.beginPath();
        ctx.rect(
                prop.getX() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getY() + RendererProperties.PROCESS_NODE_INSET_WIDTH,
                prop.getWidth() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2,
                prop.getHeight() - RendererProperties.PROCESS_NODE_INSET_WIDTH * 2
        );
        ctx.stroke();
        ctx.fill();
        ctx.restore();
    }
}
