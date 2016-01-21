package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorInfoPanel extends Composite {

    InteractorSearchResult interactor;
    EventBus eventBus;
    FlowPanel mainPanel;

    public InteractorInfoPanel(EventBus eventBus, InteractorSearchResult interactor) {
        this.eventBus = eventBus;
        this.interactor = interactor;
        this.mainPanel = new FlowPanel();

        GraphObjectInfoPanel.SuggestionPanelCSS css = GraphObjectInfoPanel.OBJECT_INFO_RESOURCES.getCSS();
        this.init(css);

        FlowPanel header = new FlowPanel();
        header.setStyleName(css.infoHeader());
        header.add(new InlineLabel(interactor.getAcc()));
        this.add(header);
        this.add(new Label("Type: Interactor"));
        this.add(new Label("Resource: " + (interactor.getResource().equals("static") ? "Static (IntAct)" : interactor.getResource())));
        if (!interactor.getInteractsWith().isEmpty()) {
            String title = "Interacts with:";
            this.add(new DatabaseObjectListPanel(title, interactor.getInteractsWith(), eventBus));
        }
    }
    /**
     * Padding grows wider the div but this is not the expected behaviour here. To make it compatible
     * across all browsers the recommendation is having two divs
     * <div> // defines the object info panel properties
     *     <div> // defines the object info content (set the padding here)
     *         Info content
     *     </div>
     * </div>
     */
    private void init(GraphObjectInfoPanel.SuggestionPanelCSS css){
        SimplePanel sp = new SimplePanel();
        sp.setStyleName(css.objectInfoPanel());
        this.mainPanel.setStyleName(css.objectInfoContent());
        sp.add(this.mainPanel);
        initWidget(sp);
    }

    public void add(IsWidget widget){
        this.mainPanel.add(widget);
    }

}
