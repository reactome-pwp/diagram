package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGTooltip extends PopupPanel {
    private static SVGTooltip tooltip;

    private SVGTooltip() {
        this.setStyleName(RESOURCES.getCSS().popup());
    }

    public static SVGTooltip get() {
        if (tooltip == null) {
            tooltip = new SVGTooltip();
        }
        return tooltip;
    }

    public void setText(String text){
        tooltip.clear();
        tooltip.add(new InlineLabel(text != null ? text : ""));
    }

    public void hide() {
        setVisible(false);
    }

    @Override
    public void add(Widget w) {
        this.clear();
        super.add(w);
    }

    private Coordinate findOptimalPosition(double offsetX, double offsetY, double distance){
        return CoordinateFactory.get(offsetX + distance,offsetY + distance );
    }

    public void setPositionAndShow(AbsolutePanel container, double offsetX, double offsetY, double distance) {
        container.getElement().appendChild(this.getElement());

        this.setVisible(true);
        Coordinate optPosition = this.findOptimalPosition(offsetX, offsetY, distance);
        this.setPosition(optPosition.getX().intValue(), optPosition.getY().intValue());
    }

    private void setPosition(int left, int top) {
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
        String CSS = "org/reactome/web/diagram/tooltips/SVGTooltip.css";

        String popup();
    }
}
