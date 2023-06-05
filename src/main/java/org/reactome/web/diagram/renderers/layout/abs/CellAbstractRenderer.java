package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
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
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);

        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getCell().getStroke());
        ctx.setFillStyle(DiagramColours.get().PROFILE.getCell().getFill());

        fillShape(ctx, prop, node.getNeedDashedBorder());
        ctx.stroke();
        ctx.fill();

        innerShape(ctx, prop);
        ctx.stroke();
        ctx.fill();

        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getCellNucleus().getStroke());
        ctx.setFillStyle(DiagramColours.get().PROFILE.getCellNucleus().getFill());

        nucleusOuter(ctx, prop);
        ctx.stroke();
        ctx.fill();
        nucleusInner(ctx, prop);
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
        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
        double halfHeight = prop.getHeight() / 2d;
        prop = NodePropertiesFactory.get(
                prop.getX() + 2 * CELL_SEPARATION,
                prop.getY() + halfHeight + 2 * CELL_SEPARATION,
                prop.getWidth() - 4 * CELL_SEPARATION,
                prop.getHeight() / 2d - 4 * CELL_SEPARATION
        );
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        double x = prop.getX() + prop.getWidth() / 2d;
        double y = prop.getY() + prop.getHeight() / 2d;

        if (metrics.getWidth() <= prop.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), CoordinateFactory.get(x, y));
        } else {
            textRenderer.drawTextMultiLine(ctx, item.getDisplayName(), prop);
        }
    }

    private void fillShape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null && needsDashed) {
            //This is needed since the dashed rounded rectangle will always be filled
            ctx.roundedRectangle(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight(), ROUND_RECT_ARC_WIDTH);
            ctx.fill();
        }
        shape(ctx, prop, needsDashed);
    }

    private void innerShape(AdvancedContext2d ctx, NodeProperties prop) {
        ctx.roundedRectangle(
                prop.getX() + CELL_SEPARATION,
                prop.getY() + CELL_SEPARATION,
                prop.getWidth() - 2 * CELL_SEPARATION,
                prop.getHeight() - 2 * CELL_SEPARATION,
                2 * ROUND_RECT_ARC_WIDTH - CELL_SEPARATION
        );
    }

    private void nucleusOuter(AdvancedContext2d ctx, NodeProperties prop) {
        ctx.roundedRectangle(
                prop.getX() + 3 * CELL_SEPARATION,
                prop.getY() + 3 * CELL_SEPARATION,
                prop.getWidth() - 6 * CELL_SEPARATION,
                prop.getHeight() / 2 - 3 * CELL_SEPARATION,
                2 * ROUND_RECT_ARC_WIDTH - 3 * CELL_SEPARATION
        );
    }

    private void nucleusInner(AdvancedContext2d ctx, NodeProperties prop) {
        ctx.roundedRectangle(
                prop.getX() + 4 * CELL_SEPARATION,
                prop.getY() + 4 * CELL_SEPARATION,
                prop.getWidth() - 8 * CELL_SEPARATION,
                prop.getHeight() / 2 - 5 * CELL_SEPARATION,
                2 * ROUND_RECT_ARC_WIDTH - 4 * CELL_SEPARATION
        );
    }


    @Override
    public void shape(AdvancedContext2d ctx, NodeProperties prop, Boolean needsDashed) {
        if (needsDashed != null && needsDashed) {
            ctx.dashedRoundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    2 * ROUND_RECT_ARC_WIDTH,
                    DASHED_LINE_PATTERN
            );

        } else {
            ctx.roundedRectangle(
                    prop.getX(),
                    prop.getY(),
                    prop.getWidth(),
                    prop.getHeight(),
                    2 * ROUND_RECT_ARC_WIDTH
            );

        }
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
