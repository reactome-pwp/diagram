package org.reactome.web.diagram.renderers.interactor.s050;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.renderers.interactor.abs.LoopLinkAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LoopLinkRenderer050 extends LoopLinkAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        ctx.save();
        ctx.setGlobalAlpha((factor - 0.5) * 2);
        super.draw(ctx, item, factor, offset);
        ctx.restore();
    }
}
