package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class FlaggedItemsControl extends LegendPanel implements ClickHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler {
    private InlineLabel term;
    private PwpButton closeBtn;

    public FlaggedItemsControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.enrichmentControl());

        this.term = new InlineLabel();
        this.add(this.term);

        this.closeBtn = new PwpButton("Close and un-flag entities", css.close(), this);
        this.add(this.closeBtn);

        this.initHandlers();
        this.setVisible(false);
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
        this.term.setText(term + " - " + flaggedItems.size() + " entities flagged");
        this.setVisible(true);
    }

    @Override
    public void onDiagramObjectsFlagReset() {
        this.setVisible(false);
    }

    private void initHandlers() {
        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
    }

}
