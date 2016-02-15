package org.reactome.web.diagram.legends;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.interactors.InteractorsExporter;
import org.reactome.web.diagram.util.interactors.ResourceNameFormatter;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        DiagramRequestedHandler, DiagramLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler, InteractorsLayoutUpdatedHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING = "Loading interactors from ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_LOADING_PSICQUIC = "Loading PSICQUIC interactors from ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_NO_INTERACTORS_FOUND = "No interactors found in ";
    @SuppressWarnings("FieldCanBeLocal")
    private static String MSG_DOWNLOAD_TOOLTIP = "Download all diagram interactors from ";
    private static int DELAY = 5000;

    private String currentResource;

    private DiagramContext context;
    private Image loadingIcon;
    private InlineLabel message;
    private FlowPanel controlsFP;
    private Slider slider;
    private PwpButton downloadBtn;
    private PwpButton retryBtn;
    private PwpButton closeBtn;

    private Timer hideTimer;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        this.initUI();
        this.setVisible(false);

        this.initHandlers();

        hideTimer = new Timer() {
            @Override
            public void run() {
                setVisible(false);
            }
        };
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.closeBtn)) {
            //Is safe to do this here (even if there is not loading in progress because that scenario is checked by the loader)
            setVisible(false);
            hideTimer.cancel();
            if (loadingIcon.isVisible()) {
                eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
            } else {
                eventBus.fireEventFromSource(new InteractorsCollapsedEvent(currentResource), this);
            }
            setVisible(false);
        } else if(source.equals(this.downloadBtn)) {
            if (context != null) {
                MapSet<String, RawInteractor> interactors = context.getInteractors().getRawInteractorsPerResource(currentResource);
                if(interactors != null && !interactors.isEmpty()) {
                    String filename = context.getContent().getStableId() + "_Interactors_" + ResourceNameFormatter.format(currentResource)+ ".tsv";
                    InteractorsExporter.exportInteractors(filename, interactors);
                }
            }
        } else if (source.equals(this.retryBtn)) {
            // Fire event for a Resource selection to trigger reloading
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(currentResource), this);
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
        if (context.getInteractors().isInteractorResourceCached(currentResource)) {
            update();
        }

    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        context = null;
        setVisible(false);
    }

    @Override
    public void onInteractorsLayoutUpdated(InteractorsLayoutUpdatedEvent event) {
        update();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
        hideTimer.cancel();
        //context is null when the diagram is in the process of loading (loading message is meant to be displayed)
        if (context == null || !context.getInteractors().isInteractorResourceCached(event.getResource())) {
            setVisible(true);
            displayLoader(true, event.getResource());
        } else {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    update();
                }
            });
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        currentResource = event.getInteractors().getResource();
        int totalInteractorsLoaded = event.getInteractors().getEntities().size();
        if(totalInteractorsLoaded==0) {
            displayWarning(MSG_NO_INTERACTORS_FOUND + ResourceNameFormatter.format(currentResource));
            setTimer(DELAY);
        }else {
            hideTimer.cancel();
            update();
        }

    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        setVisible(true);
        switch (event.getLevel()){
            case WARNING:
                displayWarning(event.getMessage());
                setTimer(DELAY);
                break;
            case ERROR:
                displayError(event.getMessage());
                setTimer(DELAY);
                break;
            case ERROR_RECOVERABLE:
                displayError(event.getMessage());
                retryBtn.setTitle("Retry loading interactors from " + ResourceNameFormatter.format(currentResource) );
                retryBtn.setVisible(true);
                break;
        }

    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        double threshold = event.getPercentage();
        InteractorsContent.setInteractorsThreshold(currentResource, threshold);
        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
    }

    private void update() {
        int burstEntities = context.getContent().getNumberOfBurstEntities();
        if (burstEntities == 0) {
            setVisible(false);
        } else {
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
            setVisible(true);
            setMessage(ResourceNameFormatter.format(currentResource));
            controlsFP.setVisible(true);
            retryBtn.setVisible(false);
            slider.setValue(InteractorsContent.getInteractorsThreshold(currentResource));
            downloadBtn.setTitle(MSG_DOWNLOAD_TOOLTIP + ResourceNameFormatter.format(currentResource));
        }
    }

    private void initUI() {
        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());
        addStyleName(RESOURCES.getCSS().unselectable());

        loadingIcon = new Image(RESOURCES.loader());
        loadingIcon.setStyleName(RESOURCES.getCSS().interactorsControlLoadingIcon());
        message = new InlineLabel("");
        message.setStyleName(RESOURCES.getCSS().interactorsControlMessage());

        closeBtn = new PwpButton("Close and clear interactors", RESOURCES.getCSS().close(), this);
        downloadBtn = new PwpButton(MSG_DOWNLOAD_TOOLTIP, RESOURCES.getCSS().download(), this);
        retryBtn = new PwpButton("Retry loading interactors", RESOURCES.getCSS().download(), this);

        slider = new Slider(100, 24, 0.45, 1, 0.45, true);
        slider.setTooltip("Use this slider to set the confidence threshold");
        slider.addSliderValueChangedHandler(this);
        slider.setStyleName(RESOURCES.getCSS().interactorsControlSlider());

        controlsFP = new FlowPanel();
        controlsFP.setStyleName(RESOURCES.getCSS().interactorsControlControls());
        controlsFP.add(this.slider);
        controlsFP.add(this.downloadBtn);

        add(loadingIcon);
        add(message);
        add(closeBtn);
        add(controlsFP);
        //add(retryBtn);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLayoutUpdatedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
    }

    private void displayLoader(boolean visible, String resource) {
        this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        loadingIcon.setVisible(visible);
        controlsFP.setVisible(!visible);
        retryBtn.setVisible(!visible);
        if (visible) {
            String msg;
            if(resource.equals(DiagramFactory.INTERACTORS_INITIAL_RESOURCE)) {
                msg = MSG_LOADING + ResourceNameFormatter.format(DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME);
            } else {
                msg = MSG_LOADING_PSICQUIC + ResourceNameFormatter.format(resource);
            }
            message.setText(msg + "...");
        }
    }

    private void displayError(String serverMsg) {
        if (serverMsg == null) {
            displayError(false);
        } else {
            setMessage(serverMsg);
            displayError(true);
        }
    }

    private void displayError(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlError());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlWarning());
        }
        controlsFP.setVisible(!visible);
        retryBtn.setVisible(!visible);
    }

    private void displayWarning(String msg) {
        setMessage(msg);
        displayWarning(true);
    }

    private void displayWarning(boolean visible) {
        if (visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlWarning());
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        }
        controlsFP.setVisible(!visible);
        retryBtn.setVisible(!visible);
    }

    private void setMessage(String msg) {
        loadingIcon.setVisible(false);
        message.setText(msg);
    }

    public void setTimer(int millis) {
        hideTimer.cancel();
        hideTimer.schedule(millis);
    }

}
