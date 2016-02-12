package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.controls.settings.common.InfoLabel;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawResource;
import org.reactome.web.diagram.data.interactors.raw.factory.ResourcesException;
import org.reactome.web.diagram.data.loader.InteractorsResourceLoader;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.interactors.InteractorsExporter;
import org.reactome.web.diagram.util.interactors.ResourceNameFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ClickHandler, ValueChangeHandler, InteractorsResourceLoader.Handler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler,
        DiagramLoadedHandler, DiagramRequestedHandler {
    private static int RESOURCES_REFRESH = 600000; // Update every 10 minutes

    private EventBus eventBus;
    private DiagramContext context;
    private List<RawResource> resourcesList = new ArrayList<>();

    private RadioButton staticResourceBtn;
    private FlowPanel liveResourcesFP;
    private FlowPanel loadingPanel;
    private IconButton downloadBtn;
    private String selectedResource;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        InfoLabel tabHeader = new InfoLabel("Interactor Overlays", RESOURCES.aboutThis());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        staticResourceBtn = new RadioButton("Resources", DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME);
        staticResourceBtn.setFormValue(DiagramFactory.INTERACTORS_INITIAL_RESOURCE); //use FormValue to keep the value
        staticResourceBtn.setTitle("Select " + DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME + " as a resource");
        staticResourceBtn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
        staticResourceBtn.setValue(true);
        staticResourceBtn.addValueChangeHandler(this);

        selectedResource = staticResourceBtn.getFormValue();

        // Loading panel
        Image loadingSpinner = new Image(RESOURCES.loadingSpinner());
        loadingPanel = new FlowPanel();
        loadingPanel.add(loadingSpinner);
        loadingPanel.add(new InlineLabel(" Updating resources..."));
        loadingPanel.setStyleName(RESOURCES.getCSS().loadingPanel());

        Label liveResourcesLabel = new Label("PSICQUIC:");
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
        if (InteractorsExporter.fileSaveScriptAvailable()) {
            String resourceName = ResourceNameFormatter.format(selectedResource);
            downloadBtn = new IconButton(resourceName, RESOURCES.downloadNormal());
            downloadBtn.addClickHandler(this);
            downloadBtn.setTitle("Click to download all diagram interactors from " + resourceName);
            downloadBtn.setStyleName(RESOURCES.getCSS().downloadBtn());
            downloadBtn.setVisible(false);
            main.add(downloadBtn);
        } else {
            Console.warn("FileSaver script has not been not loaded");
        }
        initWidget(main);
        initialiseHandlers();

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
    public void onClick(ClickEvent event) {
        if (context != null) {
            MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(selectedResource);
            if(hasContents(interactors)) {
                String filename = context.getContent().getStableId() + "_Interactors_" + ResourceNameFormatter.format(selectedResource)+ ".tsv";
                InteractorsExporter.exportInteractors(filename, interactors);
            }
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        context = null;
        showDownloadButton(false);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
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
    public void onInteractorsError(InteractorsErrorEvent event) {
        showDownloadButton(false);
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        showDownloadButton(true);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        if(context!=null) {
            String resourceName = ResourceNameFormatter.format(event.getResource());
            downloadBtn.setText(resourceName);
            downloadBtn.setTitle("Click to download all diagram interactors from " + resourceName);
            // check
            showDownloadButton(context.getInteractors().isResourceLoaded(event.getResource()));
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        selectedResource = selectedBtn.getFormValue(); // Keep current selection
        // Fire event for a Resource selection
        eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(selectedResource), this);
    }

    private void initialiseHandlers() {
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    private void setResourcesList(List<RawResource> inputList) {
        resourcesList.clear();
        resourcesList.addAll(inputList);
        populateResourceListPanel();
    }

    private void populateResourceListPanel() {
        if (!resourcesList.isEmpty()) {
            liveResourcesFP.clear();
            for (RawResource resource : resourcesList) {
                RadioButton radioBtn = new RadioButton("Resources", ResourceNameFormatter.format(resource.getName()));
                radioBtn.setFormValue(resource.getName()); //use FormValue to keep the value
                radioBtn.addValueChangeHandler(this);
                radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtn());

                if (!resource.getActive()) {
                    radioBtn.setEnabled(false);
                    radioBtn.setTitle(resource.getName() + " is not currently available");
                    radioBtn.setStyleName(RESOURCES.getCSS().interactorResourceListBtnDisabled());
                } else {
                    radioBtn.setTitle("Select " + resource.getName() + " as a resource");
                }

                // Restore previous selection
                if (radioBtn.getFormValue().equals(selectedResource)) {
                    radioBtn.setValue(true);
                }
                liveResourcesFP.add(radioBtn);
            }
        }
    }

    private Widget getOptionsPanel() {
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

    private void showLoading(boolean loading) {
        loadingPanel.setVisible(loading);
        liveResourcesFP.setVisible(!loading);
    }

    /***
     * Show the download button only if the rawInteractors mapset
     * for the selected resource contains entries
     * @param visible
     */
    private void showDownloadButton(boolean visible){
        if(visible) {
            if (context != null) {
                MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(selectedResource);
                if (hasContents(interactors)) {
                    downloadBtn.setVisible(true);
                } else {
                    downloadBtn.setVisible(false);
                }
            }
        } else {
            downloadBtn.setVisible(false);
        }
    }

    private boolean hasContents(MapSet<String, RawInteractor> input){
        boolean rtn = false;
        if (input != null && !input.isEmpty()) {
            for (String acc : input.keySet()) {
                if (!input.getElements(acc).isEmpty()) {
                    rtn = true;
                    break;
                }
            }
        }
        return rtn;
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

        @Source("../tabs/InteractorsInfo.txt")
        TextResource aboutThis();

        @Source("../images/download_normal.png")
        ImageResource downloadNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String interactorResourceBtn();

        String liveResourcesOuterPanel();

        String liveResourcesInnerPanel();

        String loadingPanel();

        String interactorResourceListBtn();

        String interactorResourceListBtnDisabled();

        String downloadBtn();

    }
}