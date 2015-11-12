package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite {
    private EventBus eventBus;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());
        main.add(tabHeader);

        initWidget(main);
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

    @CssResource.ImportedWithPrefix("diagram-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String tabHeader();
    }
}
