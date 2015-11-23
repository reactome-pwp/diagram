package org.reactome.web.diagram.legends;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.common.PwpButton;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsControl extends LegendPanel implements ClickHandler{

    private InlineLabel message;
    private PwpButton displayAllBtn;
    private PwpButton showDetailsBtn;
    private PwpButton closeBtn;

    public InteractorsControl(EventBus eventBus) {
        super(eventBus);

        addStyleName(RESOURCES.getCSS().analysisControl());
        addStyleName(RESOURCES.getCSS().interactorsControl());

        this.message = new InlineLabel("RESOURCE");
        this.displayAllBtn = new PwpButton("Show/Hide All interactors", RESOURCES.getCSS().play(), this);
        this.showDetailsBtn = new PwpButton("Show details", RESOURCES.getCSS().play(), this);
        this.closeBtn = new PwpButton("Close", RESOURCES.getCSS().close(), this);

        this.add(this.message);
        this.add(this.displayAllBtn);
        this.add(this.showDetailsBtn);
        this.add(this.closeBtn);
        this.setVisible(true);
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();

        if (source.equals(this.closeBtn))
            this.setVisible(false);
//            eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
    }
}
