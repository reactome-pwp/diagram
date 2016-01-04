package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class FlaggedItemsControl extends LegendPanel implements ClickHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler,
        AnalysisResultLoadedHandler, AnalysisResetHandler {
    private InlineLabel term;
    private PwpButton closeBtn;

    public FlaggedItemsControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.flaggedItemsControl());

        this.term = new InlineLabel();
        this.term.setStyleName(RESOURCES.getCSS().flaggedItemsLabel());
        this.add(this.term);

        this.closeBtn = new PwpButton("Close and un-flag entities", css.close(), this);
        this.add(this.closeBtn);

        this.initHandlers();
        this.setVisible(false);
    }


    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        this.removeStyleName(RESOURCES.getCSS().flaggedItemsControlMovedUp());
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        this.addStyleName(RESOURCES.getCSS().flaggedItemsControlMovedUp());
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(this.closeBtn)){
            eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
        }
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        String term = event.getTerm();
        Set<DiagramObject> flaggedItems =  event.getFlaggedItems();
        String msg = " - " + flaggedItems.size() + (flaggedItems.size() == 1 ? " entity" : " entities") + " flagged";
        this.term.setText(term + msg);
        this.setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        this.setVisible(false);
    }

    private void initHandlers() {
        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
//        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
//        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
    }
}
