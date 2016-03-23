package org.reactome.web.diagram.renderers.layout.s000;

import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.layout.abs.ProteinAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ProteinRenderer000 extends ProteinAbstractRenderer {

    @Override
    public void drawHitInteractors(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        SummaryItem summaryItem = node.getInteractorsSummary();
        if (summaryItem != null && summaryItem.getHit() != null && summaryItem.getHit()) {
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            ctx.beginPath();
            double l = 30 * factor;
            double x = prop.getX() + prop.getWidth();
            ctx.moveTo(x - l, prop.getY() + factor);
            ctx.lineTo(x, prop.getY() + factor);
            ctx.lineTo(x - factor, prop.getY() + l);
            ctx.closePath();
            ctx.fill();
        }
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //No text at this level
    }
}
