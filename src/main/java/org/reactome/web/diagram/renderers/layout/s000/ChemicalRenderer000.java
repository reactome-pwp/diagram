package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.layout.abs.ChemicalAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ChemicalRenderer000 extends ChemicalAbstractRenderer {

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //No text at this level
    }

    @Override
    public void drawHitInteractors(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        SummaryItem summaryItem = node.getInteractorsSummary();
        if (summaryItem != null && summaryItem.getHit() != null && summaryItem.getHit()) {
            ctx.save();
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            shape(ctx, prop, false);
            ctx.clip();
            ctx.beginPath();
            double l = 40 * factor;
            double x = prop.getX() + prop.getWidth();
            ctx.moveTo(x - l, prop.getY() + factor);
            ctx.lineTo(x, prop.getY() + factor);
            ctx.lineTo(x - factor, prop.getY() + l);
            ctx.closePath();
            ctx.fill();
            ctx.restore();
        }
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        Boolean isTrivial = ((Node) item).getTrivial();
        return isTrivial == null || !isTrivial;
    }
}
