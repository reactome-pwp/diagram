package org.reactome.web.diagram.renderers.layout.s300;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphSimpleEntity;
import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.category.ShapeCategory;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.ChemicalAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ChemicalRenderer300 extends ChemicalAbstractRenderer {
    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        super.draw(ctx, item, factor, offset);
        drawSummaryItems(ctx, (Node) item, factor, offset);
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        drawChemicalDetails(ctx, item, factor, offset);
    }

    @Override
    public HoveredItem getHovered(DiagramObject item, Coordinate pos) {
        Node node = (Node) item;

        SummaryItem interactorsSummary = node.getInteractorsSummary();
        if (interactorsSummary != null) {
            if (ShapeCategory.isHovered(interactorsSummary.getShape(), pos)) {
                return new HoveredItem(node.getId(), interactorsSummary);
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

    private void drawChemicalDetails(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        ctx.save();
        Node node = (Node) item;
        GraphObject graphObject = node.getGraphObject();
        if(graphObject instanceof GraphSimpleEntity) {
            DiagramBox box = new DiagramBox(node.getProp()).transform(factor, offset);

            GraphSimpleEntity se = (GraphSimpleEntity) graphObject;
            if (se.getChemicalImage() != null) {
                Coordinate pos = CoordinateFactory.get(box.getMinX(), box.getMinY());
                double delta = box.getHeight() * 0.8; // Shrink the image in order to make it fit into the bubble
                ctx.drawImage(se.getChemicalImage(), pos.getX(), pos.getY(), delta, delta);
            }

            String displayName = node.getDisplayName();
            DiagramBox textBox = box.splitHorizontally(box.getWidth() / 2).get(1); //box is now the remaining of item box removing the image
            TextRenderer textRenderer = new TextRenderer(RendererProperties.INTERACTOR_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
            textRenderer.drawTextMultiLine(ctx, displayName, NodePropertiesFactory.get(textBox));
        }
        ctx.restore();
    }
}
