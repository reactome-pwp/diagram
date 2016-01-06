package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.client.InteractorsManager;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.InteractorsStatus;
import org.reactome.web.diagram.events.InteractorsRequestCanceledEvent;
import org.reactome.web.diagram.events.InteractorsStatusChangedEvent;
import org.reactome.web.diagram.handlers.InteractorsRequestCanceledHandler;
import org.reactome.web.diagram.handlers.InteractorsStatusChangedHandler;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        InteractorsStatusChangedHandler, InteractorsRequestCanceledHandler {
    private static String MSG_LOADING = "Loading interactors...";
    private static String MSG_NOT_LOADED = "Interactors not loaded for";

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
            InteractorsManager.get().close();
        }
    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
//        threshold = event.getPercentage();
//        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
    }

    private void initUI(){
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

    private void initHandlers(){
        eventBus.addHandler(InteractorsStatusChangedEvent.TYPE, this);
    }

    private void displayLoader(boolean visible){
        this.removeStyleName(RESOURCES.getCSS().interactorsControlError());
        loadingIcon.setVisible(visible);
        controlsFP.setVisible(!visible);
        if(visible){
           message.setText(MSG_LOADING);
        }
    }

    private void displayError(String serverMsg){
        if (serverMsg == null) {
            displayError(false);
        }else
            displayError(true);
    }

    private void displayError(boolean visible){
        if(visible) {
            this.addStyleName(RESOURCES.getCSS().interactorsControlError());
        }
        controlsFP.setVisible(!visible);
    }

    private void setMessage(String resource, String serverMsg){
        String msg = serverMsg != null ?  serverMsg : resource;
        message.setText(msg);
    }

    @Override
    public void onInteractorsRequestCanceled(InteractorsRequestCanceledEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onInteractorsStatusChangedEvent(InteractorsStatusChangedEvent event) {
        InteractorsStatus status = event.getInteractorsStatus();
        if (status == null) {
            setVisible(false);
        } else {
            setVisible(status.isVisible());
            displayLoader(status.isLoading());
            if(!status.isLoading()) {
                displayError(status.getServerMsg());
                setMessage(status.getResource(), status.getServerMsg());
                slider.setValue(status.getThreshold());
            }
        }
    }
}
