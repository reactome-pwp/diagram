package org.reactome.web.diagram.renderers.impl.s100;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.impl.abs.ShadowAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ShadowRenderer100 extends ShadowAbstractRenderer {

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }
}
