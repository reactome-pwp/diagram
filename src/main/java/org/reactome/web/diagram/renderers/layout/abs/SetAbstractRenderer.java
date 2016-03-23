package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class SetAbstractRenderer extends NodeAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        fillShape(ctx, prop, node.getNeedDashedBorder());
        ctx.fill();
        ctx.stroke();

        innerShape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        drawCross(ctx, node, prop);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        innerShape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null) {
            ctx.dashedRoundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        } else {
            ctx.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        }
    }

    protected void fillShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null && needsDashed) {
            //This is needed since the dashed rounded rectangle will always be filled
            ctx.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
            ctx.fill();
            ctx.dashedRoundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH, RendererProperties.DASHED_LINE_PATTERN);
        } else {
            ctx.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), RendererProperties.ROUND_RECT_ARC_WIDTH);
        }
    }

    protected void innerShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null) {
            ctx.dashedRoundedRectangle(
                    prop.getX() + RendererProperties.SEPARATION,
                    prop.getY() + RendererProperties.SEPARATION,
                    prop.getWidth() - RendererProperties.SEPARATION * 2,
                    prop.getHeight() - RendererProperties.SEPARATION * 2,
                    RendererProperties.ROUND_RECT_ARC_WIDTH,
                    RendererProperties.DASHED_LINE_PATTERN);
        } else {
            ctx.roundedRectangle(
                    prop.getX() + RendererProperties.SEPARATION,
                    prop.getY() + RendererProperties.SEPARATION,
                    prop.getWidth() - RendererProperties.SEPARATION * 2,
                    prop.getHeight() - RendererProperties.SEPARATION * 2,
                    RendererProperties.ROUND_RECT_ARC_WIDTH
            );
        }
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getEntityset());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getEntityset());
    }
}
