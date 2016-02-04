package org.reactome.web.diagram.renderers.interactor.s100;

import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.renderers.interactor.abs.InteractorEntityAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntityRenderer100 extends InteractorEntityAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;

        //ToDo: draw text?
        InteractorEntity entity = (InteractorEntity) item;
        if (entity.getImage() != null) {
            Coordinate pos = CoordinateFactory.get(item.getMinX(), item.getMinY()).transform(factor, offset);
            double delta = (item.getMaxY() - item.getMinY()) * factor;
            ctx.drawImage(entity.getImage(), pos.getX(), pos.getY(), delta, delta);
        }
    }
}
