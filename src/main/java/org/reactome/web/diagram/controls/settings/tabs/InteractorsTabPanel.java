package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ValueChangeHandler {
    private EventBus eventBus;
    private List<ResourceObject> resourcesList = new ArrayList<>();

    private RadioButton staticResourceBtn;
    private FlowPanel liveResourcesFP;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        staticResourceBtn = new RadioButton("Resources", "Resource1");
        staticResourceBtn.setTitle("Select IntAct as a resource");
        staticResourceBtn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
        staticResourceBtn.setValue(true);
        staticResourceBtn.addValueChangeHandler(this);

        Label liveResourcesLabel = new Label("PSICQUIC");
        liveResourcesLabel.setTitle("Select one of the PSICQUIC resources");
        liveResourcesLabel.setStyleName(RESOURCES.getCSS().interactorResourceBtn());

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        main.add(staticResourceBtn);
        main.add(liveResourcesLabel);
        main.add(getOptionsPanel());
        initWidget(main);

        //TODO: remove this and get the resources from the server
        setResourcesList(Arrays.asList( "Resource2", "Resource3", "Resource4", "Resource5", "Resource6", "Resource7", "Resource8", "Resource9"));
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        String name = selectedBtn.getText();
        // Fire event for a Resource selection
        eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(name), this);
    }

    private void setResourcesList(List<String> inputList){
        resourcesList.clear();
        for(int i=0; i<inputList.size(); i++) {
            resourcesList.add(new ResourceObject(i, inputList.get(i), i == 0));
        }
        populateResourceListPanel();
    }

    private void populateResourceListPanel(){
        if(!resourcesList.isEmpty()){
            liveResourcesFP.clear();
            for(ResourceObject resource:resourcesList){
                RadioButton radioBtn = new RadioButton("Resources", resource.getName());
                radioBtn.addValueChangeHandler(this);
                radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtn());
                if(!resource.isStatus()){
                    radioBtn.setEnabled(false);
                    radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                }
                liveResourcesFP.add(radioBtn);
            }
        }
    }

    private Widget getOptionsPanel(){
        liveResourcesFP = new FlowPanel();
        liveResourcesFP.setStyleName(RESOURCES.getCSS().liveResourcesInnerPanel());

        SimplePanel sp = new SimplePanel();
        sp.setStyleName(RESOURCES.getCSS().liveResourcesOuterPanel());
        sp.add(liveResourcesFP);
        return sp;
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/active.gif")
        ImageResource active();

        @Source("../images/inactive.png")
        ImageResource inActive();
    }

    @CssResource.ImportedWithPrefix("diagram-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String interactorResourceBtn();

        String tabHeader();

        String liveResourcesOuterPanel();

        String liveResourcesInnerPanel();

        String interactorResourceListBtn();

        String interactorResourceListBtnDisabled();

    }
}
