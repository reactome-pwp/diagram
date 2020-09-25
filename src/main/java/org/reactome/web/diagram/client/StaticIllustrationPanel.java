package org.reactome.web.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.reactome.web.diagram.common.PwpButton;


/**
 * This panel is displayed when the user selects one of the static illustrations (figures)
 */
public class StaticIllustrationPanel extends AbsolutePanel implements RequiresResize, ClickHandler {

    private ScrollPanel container;

    public StaticIllustrationPanel(){
        setStyleName(RESOURCES.getCSS().panelHidden());
        this.getElement().addClassName("pwp-DiagramCanvas");
        container = new ScrollPanel();
        container.setStyleName(RESOURCES.getCSS().container());
    }

    public void create(String url) {
        this.getElement().addClassName("pwp-DiagramCanvas");
        container.clear();
        Image img = new Image(url);
        img.getElement().addClassName("pwp-DiagramCanvas");
        container.add(img);
        add(container);
        add(new PwpButton("Close", RESOURCES.getCSS().close(), this));
        onResize();
    }

    public void setStaticIllustrationUrl(String url){
        this.clear();
        create(url);
//        setSvg(null);
    }

    @Override
    public void clear() {
        super.clear();
//        svg = null;
    }

//    public void setSvg(OMSVGSVGElement svg) {
//        if(svg == null) {
//            return;
//        }
//
//        this.svg = svg;
//        Element e  = this.container.getElement();
//        if(e.getChildCount()>1) {
//            e.replaceChild(svg.getElement(), e.getLastChild());
//        } else {
//            e.appendChild(svg.getElement());
//        }
//
//        this.getElement().addClassName("pwp-DiagramCanvas");
//        add(new PwpButton("Close", RESOURCES.getCSS().close(), this));
//        onResize();
//    }


    public void toggle(){
        if (this.getStyleName().contains(StaticIllustrationPanel.RESOURCES.getCSS().panelShown())) {
            this.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelHidden());
        } else {
            this.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelShown());
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        reset();
    }

    @Override
    public void onResize() {
        container.setWidth(getElement().getStyle().getWidth());
        container.setHeight(getElement().getStyle().getWidth());
    }

    public void reset(){
        setStyleName(RESOURCES.getCSS().panelHidden());
        this.getElement().addClassName("pwp-DiagramCanvas");
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-StaticIllustrationsPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/client/StaticIllustrationsPanel.css";

        String panelHidden();

        String panelShown();

        String container();

        String close();
    }
}
