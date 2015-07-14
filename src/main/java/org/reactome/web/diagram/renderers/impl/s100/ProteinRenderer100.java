package org.reactome.web.diagram.renderers.impl.s100;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.renderers.impl.abs.ProteinAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ProteinRenderer100 extends ProteinAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if(!isVisible(item)) return;
        super.draw(ctx, item, factor, offset);
        Node node = (Node) item;
//        shape(ctx, node, factor, offset);
//        NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
//        Coordinate centre = node.getPosition().transform(factor, offset);
//        CanvasGradient radgrad = ctx.createRadialGradient(centre.getX(), centre.getY(), factor, centre.getX(), centre.getY(), prop.getWidth());
//        radgrad.addColorStop(0, "#BBDDD6");
//        radgrad.addColorStop(0.3f, DiagramColours.get().PROFILE.getProtein().getFill());
//        ctx.setFillStyle(radgrad);
//        ctx.fill();
        drawAttachments(ctx, node, factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}
