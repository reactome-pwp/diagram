package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        DiagramRequestedHandler, DiagramLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler, InteractorsErrorHandler,
        EntityDecoratorSelectedHandler {

    private static String MSG_LOADING = "Loading interactors for ";

    private String currentResource;

    private DiagramContext context;
    private Image loadingIcon;
    private InlineLabel message;
    private FlowPanel controlsFP;
    private Slider slider;
    private PwpButton closeBtn;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        this.initUI();
        this.setVisible(false);

        this.initHandlers();
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.closeBtn)) {
            //Is safe to do this here (even if there is not loading in progress because that scenario is checked by the loader)
            setVisible(false);
            if (loadingIcon.isVisible()) {
                eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
            } else {
                eventBus.fireEventFromSource(new InteractorsCollapsedEvent(currentResource), this);
            }
        }
        setVisible(false);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        currentResource = null;
        context = null;
    }

    @Override
    public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
        SummaryItem summary = event.getSummaryItem();
        if (summary != null && summary.getType().equals("TR")) {
            update();
        }
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
        //context is null when the diagram is in the process of loading (loading message is meant to be displayed)
        if (context == null || !context.getContent().isInteractorResourceCached(event.getResource())) {
            setVisible(true);
            displayLoader(true, event.getResource());
        } else {
            update();
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        currentResource = event.getInteractors().getResource();
        update();
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        setVisible(true);
        displayError(event.getMessage());
    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        double threshold = event.getPercentage();
        DiagramContent.setInteractorsThreshold(currentResource, threshold);
        //TODO: Consider removing the event
        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
    }

    private void update() {
        int burstEntities = context.getContent().getNumberOfBustEntities(currentResource);
        if (burstEntities == 0) {
            setVisible(false);
        } else {
            this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
            setVisible(true);
            setMessage(currentResource);
            controlsFP.setVisible(true);
            this.slider.setValue(DiagramContent.getInteractorsThreshold(currentResource));
        }
    }

    private void initUI() {
        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());

        this.loadingIcon = new Image(RESOURCES.loader());
        this.loadingIcon.setStyleName(RESOURCES.getCSS().interactorsControlLoadingIcon());

        this.message = new InlineLabel("");
        this.message.setStyleName(RESOURCES.getCSS().interactorsControlMessage());
        this.closeBtn = new PwpButton("Close and clear interactors", RESOURCES.getCSS().close(), this);

        this.slider = new Slider(100, 24, 0.5, true);
        this.slider.addSliderValueChangedHandler(this);
        this.slider.setStyleName(RESOURCES.getCSS().interactorsControlSlider());

        this.controlsFP = new FlowPanel();
        this.controlsFP.setStyleName(RESOURCES.getCSS().interactorsControlControls());
        this.controlsFP.add(this.slider);

        this.add(this.loadingIcon);
        this.add(this.message);
        this.add(this.controlsFP);
        this.add(this.closeBtn);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
    }

    private void displayLoader(boolean visible, String resource) {
        this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        loadingIcon.setVisible(visible);
        controlsFP.setVisible(!visible);
        if (visible) {
            message.setText(MSG_LOADING + resource + "...");
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
        }
        controlsFP.setVisible(!visible);
    }

    private void setMessage(String msg) {
        loadingIcon.setVisible(false);
        message.setText(msg);
    }
}
