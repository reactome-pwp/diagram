package org.reactome.web.diagram.renderers.impl.s000;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Shadow;
import org.reactome.web.diagram.renderers.impl.abs.ShadowAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ShadowRenderer000 extends ShadowAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Shadow shadow = (Shadow) item;

        shape(ctx, shadow, factor, offset);
        ctx.setFillStyle(shadow.getColour());
        ctx.setStrokeStyle(shadow.getColour());
        ctx.setGlobalAlpha(0.15);
        ctx.fill();
        ctx.setGlobalAlpha(1);
        ctx.stroke();
        ctx.setStrokeStyle(shadow.getColour());
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Shadow shadow = (Shadow) item;

        ctx.setGlobalAlpha(1);
        ctx.setFillStyle(shadow.getColour());
        super.drawText(ctx, item, factor, offset);
    }
}
