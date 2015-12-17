package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ChangeHandler {
    private EventBus eventBus;
    private ListBox resourcesLB;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        resourcesLB = new ListBox();
        resourcesLB.setMultipleSelect(false);
        setResourcesList(Arrays.asList("Resource1", "Resource2", "Resource3"));
        resourcesLB.addChangeHandler(this);

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        main.add(resourcesLB);
        initWidget(main);
    }

    @Override
    public void onChange(ChangeEvent event) {
        eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(resourcesLB.getSelectedItemText()), this);
    }

    private void setResourcesList(List<String> resourcesList){
        for(String name : resourcesList){
            resourcesLB.addItem(name);
        }
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
