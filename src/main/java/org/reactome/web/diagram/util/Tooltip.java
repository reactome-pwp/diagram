package org.reactome.web.diagram.util;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Tooltip extends PopupPanel {
    private static Tooltip tooltip;

    private boolean preventShowing = false;

    private Tooltip(){
        super(true);
    }

    public static Tooltip getTooltip(){
        if(tooltip==null){
            tooltip = new Tooltip();
        }
        return tooltip;
    }

    public void show(CanvasElement sender, int offsetX, int offsetY, double nodeSize, Widget widget) {
        if(preventShowing) return; //If the node is not visible, preventShowing has to be set to false previously

        //setStyleName clears previously set popup types
        DiagramStyleFactory.FireworksStyle style = DiagramStyleFactory.getAnalysisStyle();
        style.ensureInjected();
        setStyleName(style.popup());
        sender.getParentElement().getStyle().setOverflow(Style.Overflow.VISIBLE);

        clear();
        add(widget);

        setVisible(false);
        super.show();
        int left; int top;
        int size = (int) Math.ceil(nodeSize) + 4;
        if(offsetX < sender.getWidth()/2) {
            left = sender.getAbsoluteLeft() + offsetX - 12;
            if((offsetY - size) < 50){
                top = sender.getAbsoluteTop() + offsetY + size;
                addStyleName(DiagramStyleFactory.getAnalysisStyle().popupTopLeft());
            }else{
                top = sender.getAbsoluteTop() + offsetY - getOffsetHeight() - size;
                addStyleName(DiagramStyleFactory.getAnalysisStyle().popupBottomLeft());
            }
        }else{
            left = sender.getAbsoluteLeft() + offsetX - getOffsetWidth() + 12;
            if((offsetY - size)< 50){
                top = sender.getAbsoluteTop() + offsetY + size;
                addStyleName(DiagramStyleFactory.getAnalysisStyle().popupTopRight());
            }else{
                top = sender.getAbsoluteTop() + offsetY - getOffsetHeight() - size;
                addStyleName(DiagramStyleFactory.getAnalysisStyle().popupBottomRight());
            }
        }
        setPopupPosition(left, top);
        setVisible(true);
    }

    public void setPreventShowing(boolean preventShowing) {
        this.preventShowing = preventShowing;
        if(preventShowing && isVisible()) this.hide();
    }
}