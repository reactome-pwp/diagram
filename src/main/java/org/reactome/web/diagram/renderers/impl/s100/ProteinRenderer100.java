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
        drawAttachments(ctx, node, factor, offset, true);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.highlight(ctx, item, factor, offset);
        drawAttachments(ctx, (Node) item, factor, offset, false);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}
