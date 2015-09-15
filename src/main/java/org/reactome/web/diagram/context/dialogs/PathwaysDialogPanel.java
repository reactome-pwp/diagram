package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.sections.Section;
import org.reactome.web.diagram.context.sections.SectionCellSelectedEvent;
import org.reactome.web.diagram.context.sections.SectionCellSelectedHandler;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
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
    private FlowPanel container;

    public PathwaysDialogPanel(EventBus eventBus, DiagramObject diagramObject) {
        this.eventBus = eventBus;
        this.container = new FlowPanel();
        this.container.add(new InlineLabel("Loading pathways dialog content..."));
        initWidget(new ScrollPanel(this.container));
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
        if(pathways!=null && !pathways.isEmpty()){
            Section section = new Section("Other Pathways (" + pathways.size() + ")" , 100);
            List<List<String>> tableContents = new LinkedList<>();
            for (Pathway pathway : pathways) {
                List<String> row = new LinkedList<>();
                row.add(pathway.getDisplayName());
                row.add(pathway.getIdentifier());
                tableContents.add(row);
            }

            section.setTableContents(tableContents);
            section.addSectionCellSelectedHandler(this);
            container.add(section);
        } else {
            this.container.add(new Label("This entity is not found in any other pathway."));
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
        this.eventBus.fireEventFromSource(new DiagramLoadRequestEvent(value), this);
    }
}
