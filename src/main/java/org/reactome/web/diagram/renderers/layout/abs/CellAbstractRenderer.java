package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.helper.RoundedRectangleHelper;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

import static org.reactome.web.diagram.renderers.common.RendererProperties.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class CellAbstractRenderer extends NodeAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Node node = (Node) item;
        NodeProperties.Builder builder = new NodeProperties.Builder()
                .copy(node.getProp())
                .transform(factor, offset);


        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getCell().getStroke());
        ctx.setFillStyle(DiagramColours.get().PROFILE.getCell().getFill());

        double arc = 2 * ROUND_RECT_ARC_WIDTH;
        RoundedRectangleHelper helper = new RoundedRectangleHelper(builder.build()).setArc(arc);

        helper.trace(ctx);
        ctx.stroke();
        ctx.fill();

        helper.setPadding(CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();

        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getCellNucleus().getStroke());
        ctx.setFillStyle(DiagramColours.get().PROFILE.getCellNucleus().getFill());

        RoundedRectangleHelper nucleusHelper = new RoundedRectangleHelper(builder
                .height((prop) -> prop.getHeight() / 2 + 3 * CELL_SEPARATION)
                .build(), 0d, arc
        );

        nucleusHelper.setPadding(3 * CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();

        nucleusHelper.setPadding(4 * CELL_SEPARATION).trace(ctx);
        ctx.stroke();
        ctx.fill();

    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    public boolean nodeAttachmentsVisible() {
        return false;
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {

        if (item.getDisplayName() == null || item.getDisplayName().isEmpty()) {
            return;
        }
        TextMetrics metrics = ctx.measureText(item.getDisplayName());

        Node node = (Node) item;
        NodeProperties.Builder builder = new NodeProperties.Builder()
                .copy(node.getProp())
                .transform(factor, offset)
                .y(prop -> prop.getY() + prop.getHeight() / 2)
                .height(prop -> prop.getHeight() / 2)
                .padding(3 * CELL_SEPARATION);

        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);

        if (metrics.getWidth() <= builder.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), builder.getCenter());
        } else {
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), builder.build());
        }
    }


    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        new RoundedRectangleHelper(prop)
                .setArc(2 * ROUND_RECT_ARC_WIDTH)
                .trace(ctx, needsDashed);
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getCompartment());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setFont(getFont(WIDGET_FONT_SIZE));
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getCell());
    }

    protected void drawAttachments(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset, boolean fill) {
        if (!nodeAttachmentsVisible()) return;
        List<NodeAttachment> atList = node.getNodeAttachments();
        if (atList != null) {
            for (NodeAttachment nodeAttachment : atList) {
                AttachmentAbstractRenderer.draw(ctx, nodeAttachment, factor, offset, fill);
            }
        }
    }
}
