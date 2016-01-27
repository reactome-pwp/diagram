package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.util.MapSet;

import java.util.Set;

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
        header.add(new InlineLabel(interactor.getAccession()));
        this.add(header);
        this.add(new Label("Type: Interactor"));
        this.add(new Label("Resource: " + (interactor.getResource().equals("static") ? "Static (IntAct)" : formatName(interactor.getResource()))));
        MapSet<String, GraphObject> interactsWith = interactor.getInteractsWith();
        for(String interactionId:interactsWith.keySet()) {
            Set<GraphObject> interactors = interactsWith.getElements(interactionId);
            if (!interactors.isEmpty()) {
                Double score = interactor.getInteractionScore(interactionId);
                String title = "Interacts with: [" + interactionId + "] - Score: " + (score!=null ? NumberFormat.getFormat("0.000").format(score): "-");
                this.add(new DatabaseObjectListPanel(title, interactors, eventBus));
            }
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

    /**
     *  Changes the name by capitalizing the first character
     *  only in case all letters are lowercase
     */
    private String formatName(String originalName) {
        String output;
        if(originalName.equals(originalName.toLowerCase())){
            output = originalName.substring(0, 1).toUpperCase() + originalName.substring(1);
        } else {
            output = originalName;
        }
        return output;
    }
}
