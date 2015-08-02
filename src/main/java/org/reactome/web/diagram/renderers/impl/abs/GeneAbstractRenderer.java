package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class GeneAbstractRenderer extends NodeAbstractRenderer{
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        Node node = (Node) item;
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        fillTextHolder(ctx, prop);
        shape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(isVisible(item) && item.getDisplayName() != null && !item.getDisplayName().isEmpty()) {
            Node node = (Node) item;
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            Coordinate centre = CoordinateFactory.get(prop.getX() + prop.getWidth() / 2, prop.getY() + prop.getHeight() * 0.75);
            TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), centre);
        }
    }

        @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    public void fillTextHolder(AdvancedContext2d ctx, NodeProperties prop){
        ctx.geneTextHolder(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                prop.getHeight(),
                RendererProperties.GENE_SYMBOL_WIDTH,
                RendererProperties.ROUND_RECT_ARC_WIDTH
        );
        ctx.save();
        ctx.setFillStyle(DiagramColours.get().PROFILE.getGene().getFill());
        ctx.fill();
        ctx.restore();
    }

    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        ctx.geneShape(
                prop.getX(),
                prop.getY(),
                prop.getWidth(),
                prop.getHeight(),
                RendererProperties.GENE_SYMBOL_PAD,
                RendererProperties.GENE_SYMBOL_WIDTH,
                RendererProperties.ARROW_LENGTH,
                RendererProperties.ARROW_ANGLE
        );
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getGene());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getGene());
    }
}
