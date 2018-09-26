package org.reactome.web.diagram.renderers.layout.s800;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.layout.s300.ProteinDrugRenderer300;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ProteinDrugRenderer800 extends ProteinDrugRenderer300 {
    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        double w = node.getProp().getWidth();
        double h = node.getProp().getHeight();
        if (h >= 35 && w >= h * 1.5) {
            drawProteinDetails(ctx, node, factor, offset, 6.33 * factor);
        } else if (h >= 15 && w >= h * 1.2) {
            drawProteinDetails(ctx, node, factor, offset, 2.75 * factor);
            NodeProperties prop = NodePropertiesFactory.transform(node.getProp(), factor, offset);
            rxText(ctx, prop, factor);
        } else {
            super.drawText(ctx, item, factor, offset);
        }
    }

    @Override
    public boolean nodeAttachmentsVisible() {
        return true;
    }

}
