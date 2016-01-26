package org.reactome.web.diagram.renderers.interactor.abs;

import com.google.gwt.canvas.dom.client.TextMetrics;
import org.reactome.web.diagram.data.interactors.common.InteractorBox;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.interactors.InteractorColours;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.abs.TextRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorEntityAbstractRenderer extends InteractorAbstractRenderer {

    @Override
    public void shape(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        InteractorEntity entity = (InteractorEntity) item;
        InteractorBox box = item.transform(factor, offset);
        if(entity.isChemical()){
            ctx.bubble(box.getMinX(), box.getMinY(), box.getMaxX(), box.getMaxY());
        }else {
            ctx.beginPath();
            ctx.rect(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
        }
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!item.isVisible()) return;
        shape(ctx, item, factor, offset);
        if(((InteractorEntity) item).isChemical()) { //Only do extra stuff in case of chemicals (which is less likely)
            ctx.save();
            ctx.setFillStyle(InteractorColours.get().PROFILE.getChemical().getFill());
            ctx.fill();
            ctx.stroke();
            ctx.restore();
        } else {
            ctx.fill();
            ctx.stroke();
        }
    }

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset) {
        if(!item.isVisible()) return;
        InteractorEntity node = (InteractorEntity) item;
        if(node.getAccession() == null || node.getAccession().isEmpty()) return;
        InteractorBox box = item.transform(factor, offset);
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE, RendererProperties.NODE_TEXT_PADDING);
        TextMetrics metrics = ctx.measureText(node.getAccession());
        if(metrics.getWidth()<=box.getWidth() - 2 * RendererProperties.NODE_TEXT_PADDING) {
            textRenderer.drawTextSingleLine(ctx, node.getAccession(), box.getCentre());
        } else {
            textRenderer.drawTextMultiLine(ctx, node.getAccession(), NodePropertiesFactory.get(box));
        }

    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramInteractor item, Double factor, Coordinate offset){
        if(!item.isVisible()) return;
        shape(ctx, item, factor, offset);
        ctx.stroke();
    }
}
