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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ClickHandler, ValueChangeHandler, InteractorsResourceLoader.Handler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler,
        DiagramLoadedHandler, DiagramLoadRequestHandler {
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

        staticResourceBtn = new RadioButton("Resources", "Static (IntAct)");
        staticResourceBtn.setFormValue("static"); //use FormValue to keep the value
        staticResourceBtn.setTitle("Select IntAct as a resource");
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
            downloadBtn = new IconButton("Download " + formatName(selectedResource) + " Interactors", RESOURCES.downloadNormal());
            downloadBtn.addClickHandler(this);
            downloadBtn.setTitle("Click to download all diagram interactors");
            downloadBtn.setStyleName(RESOURCES.getCSS().downloadBtn());
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
            if(interactors != null && !interactors.isEmpty()) {
                String filename = context.getContent().getStableId() + "_Interactors_" + formatName(selectedResource)+ ".tsv";
                InteractorsExporter.exportInteractors(filename, interactors);
            }
        }
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        context = null;
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
        downloadBtn.setVisible(false);
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        downloadBtn.setVisible(true);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        if(context!=null) {
            downloadBtn.setText("Download " + formatName(event.getResource()) + " Interactors");
            if (context.getInteractors().isResourceLoaded(event.getResource())) {
                downloadBtn.setVisible(true);
            } else {
                downloadBtn.setVisible(false);
            }
        }
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

    private void initialiseHandlers() {
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
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
                RadioButton radioBtn = new RadioButton("Resources", formatName(resource.getName()));
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
                if (radioBtn.getText().equals(selectedResource)) {
                    radioBtn.setValue(true);
                }
                liveResourcesFP.add(radioBtn);
            }
        }
    }

    /**
     * Changes the name by capitalizing the first character
     * only in case all letters are lowercase
     */
    private String formatName(String originalName) {
        String output;
        if (originalName.equals(originalName.toLowerCase())) {
            output = originalName.substring(0, 1).toUpperCase() + originalName.substring(1);
        } else {
            output = originalName;
        }
        return output;
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
