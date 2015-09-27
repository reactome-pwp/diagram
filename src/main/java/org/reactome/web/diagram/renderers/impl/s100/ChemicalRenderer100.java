package org.reactome.web.diagram.renderers.impl.s100;

import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.impl.abs.ChemicalAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ChemicalRenderer100 extends ChemicalAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
        drawSummaryItems(ctx, (NodeCommon) item, factor, offset);
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        Node node = (Node) item;
        if(node.getSummaryItems()!=null){
            for (SummaryItem summaryItem : node.getSummaryItems()) {
                if(ShapeCategory.isHovered(summaryItem.getShape(), pos)){
                    return new HoveredItem(node.getId(), summaryItem);
                }
            }
        }
        return super.getHovered(item, pos);
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.highlight(ctx, item, factor, offset);
        drawSummaryItems(ctx, (Node) item, factor, offset);
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}
