package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.sections.Section;
import org.reactome.web.diagram.context.sections.SectionCellSelectedEvent;
import org.reactome.web.diagram.context.sections.SectionCellSelectedHandler;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.classes.PhysicalEntity;
import org.reactome.web.pwp.model.client.RESTFulClient;
import org.reactome.web.pwp.model.client.handlers.PathwaysForEntitiesLoadedHandler;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwaysDialogPanel extends Composite implements DatabaseObjectCreatedHandler, PathwaysForEntitiesLoadedHandler,
        SectionCellSelectedHandler {

    private EventBus eventBus;
    private DiagramContext context;
    private FlowPanel container;
    private GraphObject graphObject;

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
                    List<String> row = new LinkedList<>();
                    row.add(pathway.getDisplayName());
                    tableContents.add(row);
                }
            }
            if(tableContents.size()>0) {
                Section section = new Section("Other Pathways (" + pathways.size() + ")", 110);
                section.setTableContents(tableContents);
                section.addSectionCellSelectedHandler(this);
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
        String value = event.getValue();
        Console.info("Cell Selected: " + value);
        this.eventBus.fireEventFromSource(new DiagramLoadRequestEvent(value), this);
    }
}
