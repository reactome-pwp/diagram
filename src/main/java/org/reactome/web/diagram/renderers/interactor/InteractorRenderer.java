package org.reactome.web.diagram.renderers.interactor;

import org.reactome.web.diagram.data.interactors.raw.Interactor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface InteractorRenderer {

    void draw(AdvancedContext2d ctx, Interactor item, Double factor, Coordinate offset);

    void drawText(AdvancedContext2d ctx, Interactor item, Double factor, Coordinate offset);
}
