package org.reactome.web.diagram.renderers.impl.s050;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Shadow;
import org.reactome.web.diagram.renderers.impl.abs.ShadowAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ShadowRenderer050 extends ShadowAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Shadow shadow = (Shadow) item;

        ctx.save();
        double alpha = 0.4 - factor * 0.5;
        ctx.setGlobalAlpha(alpha < 0 ? 0 : alpha);

        ctx.setStrokeStyle(shadow.getColour());
        ctx.setFillStyle(shadow.getColour());
        shape(ctx, shadow, factor, offset);
        ctx.fill();
        ctx.stroke();
        ctx.restore();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Shadow shadow = (Shadow) item;

        ctx.save();
        double alpha = 1 - factor * 1.5;
        ctx.setGlobalAlpha(alpha < 0 ? 0 : alpha);
        ctx.setFillStyle(shadow.getColour());
        super.drawText(ctx, shadow, factor, offset);
        ctx.restore();
    }
}
