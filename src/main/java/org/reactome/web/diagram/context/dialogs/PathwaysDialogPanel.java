package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.sections.Section;
import org.reactome.web.diagram.context.sections.SectionCellSelectedEvent;
import org.reactome.web.diagram.context.sections.SectionCellSelectedHandler;
import org.reactome.web.diagram.context.sections.SelectionSummary;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.classes.PhysicalEntity;
import org.reactome.web.pwp.model.client.RESTFulClient;
import org.reactome.web.pwp.model.client.handlers.AncestorsCreatedHandler;
import org.reactome.web.pwp.model.client.handlers.PathwaysForEntitiesLoadedHandler;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.reactome.web.pwp.model.util.Ancestors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class PathwaysDialogPanel extends Composite implements DatabaseObjectCreatedHandler, PathwaysForEntitiesLoadedHandler,
        SectionCellSelectedHandler {

    private EventBus eventBus;
    private DiagramContext context;
    private FlowPanel container;
    private GraphObject graphObject;
    private List<Pathway> pathwaysIndex = new ArrayList<>();

    public PathwaysDialogPanel(EventBus eventBus, DiagramObject diagramObject, DiagramContext context) {
        this.eventBus = eventBus;
        this.context = context;
        this.container = new FlowPanel();
        this.container.add(new InlineLabel("Loading pathways dialog content..."));
        initWidget(new ScrollPanel(this.container));
        this.graphObject = diagramObject.getGraphObject();
        DatabaseObjectFactory.get(diagramObject.getReactomeId(), this);
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        if (databaseObject instanceof PhysicalEntity) {
            PhysicalEntity pe = databaseObject.cast();
            RESTFulClient.loadPathwaysForEntities(pe, this);
        }
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {
        this.container.clear();
        this.container.add(new Label("An error has occurred. ERROR: " + exception.getMessage()));
    }

    @Override
    public void onPathwaysForEntitiesLoaded(List<Pathway> pathways) {
        this.container.clear();
        Long dbId = context.getContent().getDbId();
        if(pathways!=null && !pathways.isEmpty()){
            List<List<String>> tableContents = new LinkedList<>();
            for (Pathway pathway : pathways) {
                if(!pathway.getDbId().equals(dbId)) {
                    pathwaysIndex.add(pathway);
                    List<String> row = new LinkedList<>();
                    row.add(pathway.getDisplayName());
                    row.add("\u25b6");
                    tableContents.add(row);
                }
            }
            if(tableContents.size()>0) {
                Section section = new Section("Other Pathways", 110);
                section.setTableContents(tableContents);
                section.addSectionCellSelectedHandler(this);
                section.setDataColumnWidth(0, 240);
                section.setDataColumnWidth(1, 9);
                container.add(section);
            } else {
                String className = graphObject.getClassName().toLowerCase();
                this.container.add(new Label("This " + className +" is not present in any other pathway."));
            }
        }
    }

    @Override
    public void onPathwaysForEntitiesError(Throwable throwable) {
        this.container.clear();
        this.container.add(new Label("An error has occurred. ERROR: " + throwable.getMessage()));
    }

    @Override
    public void onCellSelected(SectionCellSelectedEvent event) {
        SelectionSummary selection = event.getSelection();
        if(selection.getColIndex()==1) {
            final Pathway pathway = pathwaysIndex.get(selection.getRowIndex());
            if (pathway == null){
                Console.error("No pathway associated with " + selection.getRowIndex());
            } else if (pathway.getHasDiagram()) {
                eventBus.fireEventFromSource(new DiagramLoadRequestEvent(pathway), this);
            }else{
                RESTFulClient.getAncestors(pathway, new AncestorsCreatedHandler() {
                    @Override
                    public void onAncestorsLoaded(Ancestors ancestors) {
                        Pathway aux = ancestors.get(0).getLastPathwayWithDiagram();
                        eventBus.fireEventFromSource(new DiagramLoadRequestEvent(aux, pathway), PathwaysDialogPanel.this);
                    }

                    @Override
                    public void onAncestorsError(Throwable exception) {
                        Console.error("No pathway with diagram found for " + pathway);
                    }
                });
            }
        }
    }
}
