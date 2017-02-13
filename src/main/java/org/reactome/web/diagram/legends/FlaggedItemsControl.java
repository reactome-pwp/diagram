package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class FlaggedItemsControl extends LegendPanel implements ClickHandler,
        ContentRequestedHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler, DiagramObjectsFlagRequestHandler,
        AnalysisResultLoadedHandler, AnalysisResetHandler {

    private InlineLabel msgLabel;
    private PwpButton closeBtn;
    private Image loadingIcon;

    public FlaggedItemsControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.flaggedItemsControl());

        loadingIcon = new Image(RESOURCES.loader());
        loadingIcon.setStyleName(css.interactorsControlLoadingIcon());
        this.add(loadingIcon);

        this.msgLabel = new InlineLabel();
        this.msgLabel.setStyleName(RESOURCES.getCSS().flaggedItemsLabel());
        this.add(this.msgLabel);

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
        this.msgLabel.setText(term + msg);
        loadingIcon.setVisible(false);
        setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        String term = event.getTerm();
        loadingIcon.setVisible(true);
        msgLabel.setText("Flagging entities for " + term + "...");
        this.setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        this.setVisible(false);
    }

    private void initHandlers() {
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(false);
    }
}
