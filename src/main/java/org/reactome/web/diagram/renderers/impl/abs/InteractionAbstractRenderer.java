package org.reactome.web.diagram.renderers.impl.abs;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Link;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InteractionAbstractRenderer extends EdgeAbstractRenderer{
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;

        Link link = (Link) item;
        drawSegments(ctx, link.getSegments(), factor, offset);
        ShapeAbstractRenderer.draw(ctx, link.getEndShape(), factor, offset);
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        return null;
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getInteractor());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type){
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getInteractor());
    }
}
