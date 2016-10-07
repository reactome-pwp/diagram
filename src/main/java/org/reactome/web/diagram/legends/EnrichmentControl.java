package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EnrichmentControl extends LegendPanel implements ClickHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler,
        ContentRequestedHandler {

    private InlineLabel message;
    private PwpButton closeBtn;

    public EnrichmentControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.enrichmentControl());

        this.message = new InlineLabel();
        this.add(this.message);

        this.closeBtn = new PwpButton("Close", css.close(), this);
        this.add(this.closeBtn);

        this.initHandlers();
        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(this.closeBtn)){
            eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        switch (event.getType()) {
            case OVERREPRESENTATION:
            case SPECIES_COMPARISON:
                String message = event.getType().name().replaceAll("_", " ");
                this.message.setText(message.toUpperCase());
                makeVisible(200); // Appear with delay
                break;
            default:
                hide();
        }
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        hide();
    }

    private void hide(){
        if(this.isVisible()) {
            this.message.setText("");
            this.setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        hide();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.hide();
    }
}
