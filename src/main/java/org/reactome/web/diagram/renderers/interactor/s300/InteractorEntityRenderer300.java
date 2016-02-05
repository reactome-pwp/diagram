package org.reactome.web.diagram.renderers.interactor.s300;

import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.interactors.common.InteractorBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.interactor.abs.InteractorEntityAbstractRenderer;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntityRenderer300 extends InteractorEntityAbstractRenderer {

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        InteractorEntity node = (InteractorEntity) item;
        if (node.getImage() != null) {
            Coordinate pos = CoordinateFactory.get(item.getMinX(), item.getMinY()).transform(factor, offset);
            double delta = (item.getMaxY() - item.getMinY()) * factor;
            ctx.drawImage(node.getImage(), pos.getX(), pos.getY(), delta, delta);
        }

        String displayName = node.getDisplayName();
        String details = node.getDetails();

        InteractorBox box =  item.transform(factor, offset);
        //The image size is supposed to fit the height of the box (and it is a SQUARE)
        box = box.splitHorizontally(box.getHeight()).get(1); //box is now the remaining of item box removing the image
        List<InteractorBox> vBoxes = box.splitVertically(box.getHeight()/2);

        //If there is not details it means that we can use the whole right half of the box to write the alias
        InteractorBox aliasBox = details == null ? box : vBoxes.get(0);
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        TextMetrics metrics = ctx.measureText(displayName);
        if (metrics.getWidth() <= aliasBox.getWidth() - RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, displayName, aliasBox.getCentre());
        } else {
            textRenderer.drawTextMultiLine(ctx, displayName, NodePropertiesFactory.get(aliasBox));
        }

        if (details != null) {
            ctx.save();
            ctx.setFont(RendererProperties.getFont(10));
            textRenderer = new TextRenderer(10, RendererProperties.NODE_TEXT_PADDING);
            InteractorBox detailsBox = vBoxes.get(1);
            metrics = ctx.measureText(details);
            if (metrics.getWidth() <= detailsBox.getWidth() - RendererProperties.NODE_TEXT_PADDING) {
                textRenderer.drawTextSingleLine(ctx, details, detailsBox.getCentre());
            } else {
                textRenderer.drawTextMultiLine(ctx, details, NodePropertiesFactory.get(detailsBox));
            }
            ctx.restore();
        }
    }
}
