package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsEvent;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsHandler;
import org.reactome.web.diagram.context.sections.Section;
import org.reactome.web.diagram.context.sections.SectionCellSelectedEvent;
import org.reactome.web.diagram.context.sections.SectionCellSelectedHandler;
import org.reactome.web.diagram.context.sections.SelectionSummary;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Event;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.classes.PhysicalEntity;
import org.reactome.web.pwp.model.client.RESTFulClient;
import org.reactome.web.pwp.model.client.handlers.AncestorsCreatedHandler;
import org.reactome.web.pwp.model.client.handlers.PathwaysForEntitiesLoadedHandler;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.reactome.web.pwp.model.util.Ancestors;
import org.reactome.web.pwp.model.util.Path;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class PathwaysDialogPanel extends Composite implements DatabaseObjectCreatedHandler, PathwaysForEntitiesLoadedHandler,
        SectionCellSelectedHandler, AncestorsCreatedHandler, ChangeLabelsHandler {

    private EventBus eventBus;
    private Context context;
    private FlowPanel container;
    private GraphObject graphObject;
    private List<Pathway> pathwaysIndex = new ArrayList<>();

    public PathwaysDialogPanel(EventBus eventBus, DiagramObject diagramObject, Context context) {
        this.eventBus = eventBus;
        this.context = context;
        this.container = new FlowPanel();
        Image loadingIcon =new Image(RESOURCES.loader());
        loadingIcon.setStyleName(RESOURCES.getCSS().loaderIcon());
        this.container.add(loadingIcon);
        this.container.add(new InlineLabel("Loading pathways dialog content..."));
        initWidget(new ScrollPanel(this.container));
        this.graphObject = diagramObject.getGraphObject();
        DatabaseObjectFactory.get(diagramObject.getReactomeId(), this);
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        if (databaseObject instanceof PhysicalEntity) {
            PhysicalEntity pe = databaseObject.cast();
            RESTFulClient.loadPathwaysWithDiagramForEntity(pe, this);
        }else if (databaseObject instanceof Event){
            Event event = (Event) databaseObject;
            RESTFulClient.getAncestors(event, this);
        }
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {
        this.container.clear();
        this.container.add(new Label("An error has occurred. ERROR: " + exception.getMessage()));
    }

    @Override
    public void onPathwaysForEntitiesLoaded(List<Pathway> pathways) {
        Set<Pathway> rtn = new HashSet<>();
        for (Pathway pathway : pathways) {
            // Keep only those pathways belonging to the same species as the displayed diagram
            if(context.getContent().getStableId().substring(0, 5).equals(pathway.getIdentifier().substring(0,5))) {
                rtn.add(pathway);
            }
        }
        filterPathways(rtn);
        populatePathwaysTable(pathwaysIndex, false);
    }

    @Override
    public void onPathwaysForEntitiesError(Throwable throwable) {
        this.container.clear();
        this.container.add(new Label("An error has occurred. ERROR: " + throwable.getMessage()));
    }

    @Override
    public void onAncestorsLoaded(Ancestors ancestors) {
        Set<Pathway> rtn = new HashSet<>();
        for (Path ancestor : ancestors) {
            rtn.add(ancestor.getLastPathwayWithDiagram()); //We do not include subpathways in the list
        }
        filterPathways(rtn);
        populatePathwaysTable(pathwaysIndex, false);
    }

    @Override
    public void onAncestorsError(Throwable throwable) {
        this.container.clear();
        this.container.add(new Label("An error has occurred. ERROR: " + throwable.getMessage()));
    }

    @Override
    public void onCellSelected(SectionCellSelectedEvent event) {
        SelectionSummary selection = event.getSelection();
        final Pathway pathway = pathwaysIndex.get(selection.getRowIndex());
        if (pathway == null){
            Console.error("No pathway associated with " + selection.getRowIndex(), this);
        } else if (pathway.getHasDiagram()) {
            eventBus.fireEventFromSource(new ContentRequestedEvent(pathway.getDbId() + ""), this);
        } else {
            Console.error("No diagram for " + pathway.toString(), this);
        }
    }

    @Override
    public void onChangeLabels(ChangeLabelsEvent event) {
        populatePathwaysTable(pathwaysIndex, event.getShowIds());
    }

    private void filterPathways(Collection<Pathway> pathways){
        Long dbId = context.getContent().getDbId();
        if (pathways != null && !pathways.isEmpty()) {
            for (Pathway pathway : pathways) {
                if (pathway.getDbId().equals(dbId)) continue;
                if (pathway.getHasDiagram()) { // We do not include subpathways in the list
                    if (pathwaysIndex.contains(pathway)) continue;
                    pathwaysIndex.add(pathway);
                }
            }
        }
    }

    private void populatePathwaysTable(Collection<Pathway> pathways, boolean showIds){
        this.container.clear();
        List<List<String>> tableContents = new LinkedList<>();
        if (pathways != null && !pathways.isEmpty()) {
            for (Pathway pathway : pathways) {
                List<String> row = new LinkedList<>();
                if(showIds){
                    row.add(pathway.getIdentifier() + " [" + pathway.getSpecies().get(0).getDisplayName() + "]");
                } else {
                    row.add(pathway.getDisplayName() + " [" + pathway.getSpecies().get(0).getDisplayName() + "]");
                }
                row.add("\u25b6");
                tableContents.add(row);
            }
        }
        if (tableContents.size() > 0) {
            Section section = new Section("Other Pathways", 110);
            section.setTableContents(tableContents);
            section.addSectionCellSelectedHandler(this);
            section.setDataColumnWidth(0, 240);
            section.setDataColumnWidth(1, 9);
            container.add(section);
        } else {
            String className = graphObject.getClassName().toLowerCase();
            this.container.add(new Label("This " + className + " is not present in any other pathway."));
        }
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/loader.gif")
        ImageResource loader();
    }

    @CssResource.ImportedWithPrefix("diagram-PathwaysDialogPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/context/dialogs/PathwaysDialogPanel.css";

        String loaderIcon();
    }
}
