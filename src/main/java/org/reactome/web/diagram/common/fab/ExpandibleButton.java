package org.reactome.web.diagram.common.fab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ExpandibleButton extends AbsolutePanel implements ClickHandler {
    private EventBus eventBus;

    public ExpandibleButton(EventBus eventBus) {
        this.eventBus = eventBus;
        setStyleName(RESOURCES.getCSS().container());
    }


    @Override
    public void onClick(ClickEvent clickEvent) {

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

        String baseButtons();

        String optionButtons();

        String first();

    }
}
