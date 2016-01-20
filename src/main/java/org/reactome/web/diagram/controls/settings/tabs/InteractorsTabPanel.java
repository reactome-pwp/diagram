package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.interactors.raw.RawResource;
import org.reactome.web.diagram.data.interactors.raw.factory.ResourcesException;
import org.reactome.web.diagram.data.loader.InteractorsResourceLoader;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ValueChangeHandler, InteractorsResourceLoader.Handler {
    private static int RESOURCES_REFRESH = 600000; // Update every 10 minutes

    private EventBus eventBus;
    private List<RawResource> resourcesList = new ArrayList<>();

    private RadioButton staticResourceBtn;
    private FlowPanel liveResourcesFP;
    private FlowPanel loadingPanel;
    private String selectedResource;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        staticResourceBtn = new RadioButton("Resources", "Static (IntAct)");
        staticResourceBtn.setFormValue("static"); //use FormValue to keep the value
        staticResourceBtn.setTitle("Select IntAct as a resource");
        staticResourceBtn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
        staticResourceBtn.setValue(true);
        staticResourceBtn.addValueChangeHandler(this);

        // Loading panel
        Image loadingSpinner = new Image(RESOURCES.loadingSpinner());
        loadingPanel = new FlowPanel();
        loadingPanel.add(loadingSpinner);
        loadingPanel.add(new InlineLabel(" Updating resources..."));
        loadingPanel.setStyleName(RESOURCES.getCSS().loadingPanel());


        Label liveResourcesLabel = new Label("PSICQUIC");
        liveResourcesLabel.setTitle("Select one of the PSICQUIC resources");
        liveResourcesLabel.setStyleName(RESOURCES.getCSS().interactorResourceBtn());

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        main.add(staticResourceBtn);
        main.add(liveResourcesLabel);
        main.add(loadingPanel);
        main.add(getOptionsPanel());
        initWidget(main);

        loadLiveResources(); //Load resources for the first time
        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                loadLiveResources(); // Set the timer to update the resources
            }
        };
        refreshTimer.scheduleRepeating(RESOURCES_REFRESH);
    }

    @Override
    public void interactorsResourcesLoaded(List<RawResource> resourceList, long time) {
        setResourcesList(resourceList);
        showLoading(false);
    }

    @Override
    public void onInteractorsResourcesLoadError(ResourcesException exception) {
        showLoading(false);
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        // Keep current selection
        selectedResource = selectedBtn.getText();
        String value = selectedBtn.getFormValue();
        // Fire event for a Resource selection
        eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(value), this);
    }

    private void setResourcesList(List<RawResource> inputList){
        resourcesList.clear();
        resourcesList.addAll(inputList);
        populateResourceListPanel();
    }

    private void populateResourceListPanel(){
        if(!resourcesList.isEmpty()){
            liveResourcesFP.clear();
            for(RawResource resource:resourcesList){
                RadioButton radioBtn = new RadioButton("Resources", resource.getName());
                radioBtn.setFormValue(resource.getName()); //use FormValue to keep the value
                radioBtn.addValueChangeHandler(this);
                radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtn());

                if(!resource.getActive()){
                    radioBtn.setEnabled(false);
                    radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                }

                // Restore previous selection
                if(radioBtn.getText().equals(selectedResource)){
                    radioBtn.setValue(true);
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

    private void loadLiveResources() {
        showLoading(true);
        InteractorsResourceLoader.loadResources(InteractorsTabPanel.this);

    }

    private void showLoading(boolean loading){
        loadingPanel.setVisible(loading);
        liveResourcesFP.setVisible(!loading);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/loader.gif")
        ImageResource loadingSpinner();
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

        String loadingPanel();

        String interactorResourceListBtn();

        String interactorResourceListBtnDisabled();

    }
}
