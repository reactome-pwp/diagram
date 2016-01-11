package org.reactome.web.diagram.renderers.interactor.abs;

import org.reactome.web.diagram.data.interactors.common.InteractorBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorEntityAbstractRenderer extends InteractorAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!item.isVisible()) return;
        InteractorBox box = item.transform(factor, offset);
        ctx.bubble(box);
        ctx.fill();
        ctx.stroke();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {

    }
}
