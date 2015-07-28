package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphReactionLikeEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InfoPanel extends Composite {

    GraphObject graphObject;
    EventBus eventBus;
    FlowPanel content;

    public static final ObjectInfoResources OBJECT_INFO_RESOURCES;
    static {
        OBJECT_INFO_RESOURCES = GWT.create(ObjectInfoResources.class);
        OBJECT_INFO_RESOURCES.getCSS().ensureInjected();
    }

    public InfoPanel(EventBus eventBus, GraphObject graphObject) {
        this.eventBus = eventBus;
        this.graphObject = graphObject;
        this.content = new FlowPanel();

        SuggestionPanelCSS css = OBJECT_INFO_RESOURCES.getCSS();
        this.init(css);

        FlowPanel header = new FlowPanel();
        header.setStyleName(css.infoHeader());
//        header.add(new Image(databaseObject.getImageResource()));
        header.add(new InlineLabel(graphObject.getDisplayName()));
        this.add(header);

        this.add(new Label("Type: " + graphObject.getClassName()));
        if(graphObject instanceof GraphPhysicalEntity){
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            String mainId =  pe.getIdentifier();
            if(mainId!=null) {
                this.add(new Label("Identifier: " + pe.getIdentifier()));
            }
        }

        Collection<GraphReactionLikeEvent> participatesIn = new HashSet<>();
        if(!graphObject.getDiagramObjects().isEmpty()){
            String title = "Directly in the diagram:";
            this.add(new DatabaseObjectListPanel(title, Collections.singletonList(graphObject), eventBus));

            if(graphObject instanceof GraphPhysicalEntity) {
                participatesIn = ((GraphPhysicalEntity) graphObject).participatesIn();
            }else if(graphObject instanceof GraphReactionLikeEvent){
                //TODO encapsulate it into a method in ReactionLikeEvent
                Collection<GraphPhysicalEntity> rleParticipants = new HashSet<>();
                GraphReactionLikeEvent rle = (GraphReactionLikeEvent) graphObject;
                rleParticipants.addAll(rle.getInputs());
                rleParticipants.addAll(rle.getOutputs());
                rleParticipants.addAll(rle.getCatalysts());
                rleParticipants.addAll(rle.getActivators());
                rleParticipants.addAll(rle.getInhibitors());
                rleParticipants.addAll(rle.getRequirements());
                if (!rleParticipants.isEmpty()) {
                    int size = rleParticipants.size();
                    title = "Participant" + (size>1?"s(":"(") + size + "):";
                    this.add(new DatabaseObjectListPanel(title, rleParticipants, eventBus));
                }
            }

        }

        if(graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            Set<GraphPhysicalEntity> parentLocations = pe.getParentLocations();
            if (!parentLocations.isEmpty()) {
                int size = parentLocations.size();
                String title = "Part of " + size + " structure" + (size>1?"s:":":");
                this.add(new DatabaseObjectListPanel(title, parentLocations, eventBus));

                for (GraphPhysicalEntity aa : parentLocations) {
                    participatesIn.addAll(aa.participatesIn());
                }
            }
        }

        if(!participatesIn.isEmpty()){
            int size = participatesIn.size();
            String title = "Participates in " + size + " reaction" + (size>1?"s:":":");
            this.add(new DatabaseObjectListPanel(title, participatesIn, eventBus));
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
    private void init(SuggestionPanelCSS css){
        SimplePanel sp = new SimplePanel();
        sp.setStyleName(css.objectInfoPanel());
        this.content.setStyleName(css.objectInfoContent());
        sp.add(this.content);
        initWidget(sp);
    }

    public void add(IsWidget widget){
        this.content.add(widget);
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface ObjectInfoResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SuggestionPanelCSS.CSS)
        SuggestionPanelCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ObjectInfoPanel")
    public interface SuggestionPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/infopanel/InfoPanel.css";

        String objectInfoPanel();

        String objectInfoContent();

        String infoHeader();

        String databaseObjectListPanel();

        String databaseObjectListTitle();

        String databaseObjectList();

        String listItem();

        String listItemLink();
    }

}
