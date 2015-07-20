package org.reactome.web.diagram.tooltips;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.DiagramStyleFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Tooltip extends PopupPanel {
    private static Tooltip tooltip;

    private boolean preventShowing = false;

    public static Tooltip getTooltip(){
        if(tooltip==null){
            tooltip = new Tooltip();
        }
        return tooltip;
    }

    public void hide(){
        setVisible(false);
    }

    @Override
    public void add(Widget w) {
        this.clear();
        super.add(w);
        this.setStyleName(RESOURCES.getCSS().popup());
    }

    public void show(final TooltipContainer container, final DiagramObject diagramObject) {
        if(preventShowing) return; //If the node is not visible, preventShowing has to be set to false previously

        this.add(new PathwayInfoPanel(diagramObject));
        container.add(this, -1000, -1000); //Adding it where is not visible
        container.getElement().appendChild(this.getElement());
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {

                setPositionAndShow(container, diagramObject.getPosition().getX().intValue(), diagramObject.getPosition().getY().intValue(), 1.0);
            }
        });
    }

    public void setPositionAndShow(TooltipContainer container, int offsetX, int offsetY, double nodeSize) {
        this.setVisible(true);
        int left; int top; int size = (int) Math.ceil(nodeSize) + 4;
        if(offsetX < container.getWidth()/2) {
            left = offsetX - 12;
            if((offsetY - size) < 50){
                top = offsetY + size;
                this.addStyleName(RESOURCES.getCSS().popupTopLeft());
            }else{
                top = offsetY - getOffsetHeight() - size;
                this.addStyleName(RESOURCES.getCSS().popupBottomLeft());
            }
        }else{
            left = offsetX - getOffsetWidth() + 12;
            if((offsetY - size) < 50){
                top = offsetY + size;
                this.addStyleName(RESOURCES.getCSS().popupTopRight());
            }else{
                top = offsetY - getOffsetHeight() - size;
                this.addStyleName(RESOURCES.getCSS().popupBottomRight());
            }
        }
        this.setPosition(left, top);
    }

    public void setPreventShowing(boolean preventShowing) {
        this.preventShowing = preventShowing;
        if(preventShowing && isVisible()) this.hide();
    }

    private void setPosition(int left, int top){
        Element elem = getElement();
        elem.getStyle().setPropertyPx("left", left);
        elem.getStyle().setPropertyPx("top", top);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-ToolTips")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/tooltips/ToolTips.css";

        String popup();

        String popupTopLeft();

        String popupTopRight();

        String popupBottomLeft();

        String popupBottomRight();
    }
}