package org.reactome.web.diagram.renderers.interactor.abs;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLinkAbstractRenderer extends InteractorAbstractRenderer {

    public void shape(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        InteractorLink link = (InteractorLink) item;
        Coordinate from = link.getFrom().transform(factor, offset);
        Coordinate to = link.getTo().transform(factor, offset);
        ctx.beginPath();
        ctx.moveTo(from.getX(), from.getY());
        ctx.lineTo(to.getX(), to.getY());
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!item.isVisible()) return;
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {

    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }
}
