package org.reactome.web.diagram.messages;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisMessage extends MessagesPanel implements AnalysisResultRequestedHandler, AnalysisResultLoadedHandler {

    public AnalysisMessage(EventBus eventBus) {
        super(eventBus);

        MessagesPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisOverlayMessage());

        FlowPanel fp = new FlowPanel();
        fp.add(new Image(RESOURCES.loader()));
        fp.add(new InlineLabel("Loading analysis overlay..."));
        this.add(fp);
        setVisible(false);

        this.initHandlers();
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        this.setVisible(false);
    }

    private void initHandlers(){
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
    }
}
