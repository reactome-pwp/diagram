package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.Shadow;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class ShadowAbstractRenderer extends AbstractRenderer {

    public void shape(AdvancedContext2d ctx, Shadow shadow, Double factor, Coordinate offset) {
        Coordinate initial = shadow.getPoints().get(0).transform(factor, offset);
        ctx.beginPath();
        ctx.moveTo(initial.getX(), initial.getY());
        for (int i = 1; i < shadow.getPoints().size(); i++) {
            Coordinate aux = shadow.getPoints().get(i).transform(factor, offset);
            ctx.lineTo(aux.getX(), aux.getY());
        }
        ctx.closePath();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (item.getDisplayName() == null || item.getDisplayName().isEmpty()) {
            return;
        }
        Shadow shadow = (Shadow) item;
        NodeProperties prop = NodePropertiesFactory.transform(shadow.getProp(), factor, offset);

        double padding = RendererProperties.NODE_TEXT_PADDING * 2;
        padding = (prop.getWidth() - padding * 2 < 0) ? 0 : padding;
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE * 5, padding);
        double x = prop.getX() + prop.getWidth() / 2d;
        double y = prop.getY() + prop.getHeight() / 2d;

        TextMetrics metrics = ctx.measureText(item.getDisplayName());
        if (metrics.getWidth() <= prop.getWidth() - 0.5 * padding) {
            textRenderer.borderTextSingleLine(ctx, item.getDisplayName(), CoordinateFactory.get(x, y));
        } else {
            textRenderer.borderTextMultiLine(ctx, item.getDisplayName(), prop);
        }
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {

    }

    @Override
    public Long getHovered(DiagramObject item, Coordinate pos) {
        return null;
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {

    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE * 5));
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setLineWidth(0.75);
    }
}
