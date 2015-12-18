package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.interactors.raw.DiagramInteractors;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;
import org.reactome.web.diagram.handlers.InteractorsLoadedHandler;
import org.reactome.web.diagram.util.slider.Slider;
import org.reactome.web.diagram.util.slider.SliderValueChangedEvent;
import org.reactome.web.diagram.util.slider.SliderValueChangedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler, SliderValueChangedHandler,
        EntityDecoratorSelectedHandler, InteractorsLoadedHandler {
    private DiagramInteractors interactors;
    private boolean interactorsVisible = true;
    private Double threshold = 0.5;

    private InlineLabel message;
    private PwpButton showBurstsBtn;
    private Slider slider;
    private PwpButton closeBtn;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());

        this.message = new InlineLabel("RESOURCE");
        this.showBurstsBtn = new PwpButton("Show/Hide interactors", RESOURCES.getCSS().play(), this);
        this.closeBtn = new PwpButton("Close and clear interactors", RESOURCES.getCSS().close(), this);

        this.slider = new Slider(100, 24, threshold);
        this.slider.addSliderValueChangedHandler(this);
        this.slider.setVisible(true);
//        this.slider.setStyleName(css.slide());
        this.add(this.slider);

        this.add(this.message);
        this.add(this.showBurstsBtn);
        this.add(this.closeBtn);
        this.setVisible(false);

        this.initHandlers();
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(this.closeBtn)) {
            //Is safe to do this here (even if there is not loading in progress because that scenario is checked by the loader)
            eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
            this.setVisible(false);
        } else if (source.equals(this.showBurstsBtn)) {
            interactorsVisible = !interactorsVisible;
            eventBus.fireEventFromSource(new InteractorsToggledEvent(interactorsVisible), this);
        }
    }

    @Override
    public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
        if(!isVisible()){
            this.setVisible(true);
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        if(event!=null){
            interactorsVisible = true;
            interactors = event.getInteractors();
            message.setText(interactors.getResource()!=null ? interactors.getResource() : "");
        }
    }

    @Override
    public void onSliderValueChanged(SliderValueChangedEvent event) {
        threshold = event.getPercentage();
        eventBus.fireEventFromSource(new InteractorsFilteredEvent(threshold), this);
    }

    private void initHandlers(){
        eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
    }
}
