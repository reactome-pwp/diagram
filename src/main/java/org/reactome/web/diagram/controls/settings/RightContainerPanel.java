package org.reactome.web.diagram.controls.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class RightContainerPanel extends FlowPanel {

    public RightContainerPanel() {
        this.setStyleName(RESOURCES.getCSS().container());


        FlowPanel fp = new FlowPanel();
        fp.getElement().getStyle().setHeight(200, Style.Unit.PX);
        fp.getElement().getStyle().setWidth(50, Style.Unit.PX);
        fp.getElement().getStyle().setBackgroundColor("#00FF00");
        fp.getElement().getStyle().setFloat(Style.Float.LEFT);
        fp.getElement().getStyle().setMarginRight(5, Style.Unit.PX);
//        this.add(fp);
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

    @CssResource.ImportedWithPrefix("diagram-RightContainerPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/RightContainerPanel.css";

        String container();
    }
}
