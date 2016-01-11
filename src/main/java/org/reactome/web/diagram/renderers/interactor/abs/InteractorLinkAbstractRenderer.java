package org.reactome.web.diagram.renderers.interactor.abs;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.Console;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLinkAbstractRenderer extends InteractorAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        InteractorLink link = (InteractorLink) item;
        Coordinate from = link.getFrom().transform(factor, offset);
        Coordinate to = link.getTo().transform(factor, offset);
        ctx.beginPath();
        ctx.moveTo(from.getX(), from.getY());
        ctx.lineTo(to.getX(), to.getY());
        Console.log("rendering " + item);
        ctx.stroke();
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {

    }
}
