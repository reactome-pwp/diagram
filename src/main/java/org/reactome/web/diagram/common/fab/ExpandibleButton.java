package org.reactome.web.diagram.common.fab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ExpandibleButton extends AbsolutePanel {

    public ExpandibleButton() {
        super();
        setStyleName(RESOURCES.getCSS().container());

        Button b1 = new Button();
        b1.setStyleName(RESOURCES.getCSS().buttons());
        Button b2 = new Button();
        b2.setStyleName(RESOURCES.getCSS().buttons());
        Button b3 = new Button();
        b3.setStyleName(RESOURCES.getCSS().buttons());
        Button b4 = new Button();
        b4.setStyleName(RESOURCES.getCSS().buttons());

        add(b1);
        add(b2);
        add(b3);
        add(b4);
    }

    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

//        @Source("images/watermark.png")
//        ImageResource logo();
    }

    @CssResource.ImportedWithPrefix("diagram-ExpandibleButton")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/common/fab/ExpandibleButton.css";

        String container();

        String buttons();

    }
}
