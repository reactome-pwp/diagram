package org.reactome.web.diagram.search.detailspanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchPerformedEvent;
import org.reactome.web.diagram.search.SearchPerformedHandler;
import org.reactome.web.diagram.search.events.AutoCompleteRequestedEvent;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;
import org.reactome.web.diagram.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.results.InDiagramOccurrencesFactory;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.diagram.util.Console;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class DetailsInfoPanel extends AbstractAccordionPanel implements ResultSelectedHandler,
        ContentRequestedHandler, ContentLoadedHandler,
        SearchPerformedHandler, AutoCompleteRequestedHandler,
        InDiagramOccurrencesFactory.Handler {

    private EventBus eventBus;
    private Context context;
    private SearchArguments args;

    public DetailsInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        setStyleName(RESOURCES.getCSS().container());

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        //TODO Hide
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
    }

    @Override
    public void onOccurrencesSuccess(Occurrences occurrences) {
        this.clear();
        if (context == null)    return;

        if (occurrences.getInDiagram()) {
            //Selected entity is inside the diagram
            this.add(new Label("In diagram"));
//            GraphObject graphObject = context.getContent().getDatabaseObject(selectionModel.getSelectedObject().getStId());
//            eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, true), this);
        }

        List<String> occList = occurrences.getOccurrences();
        if (occList != null && !occList.isEmpty()){
            this.add(new Label("In Interacting Pathways"));
            //Selected entity is inside an encapsulated "Interacting" pathway
            GraphObject graphObject = context.getContent().getDatabaseObject(occList.get(0));
            eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, true), this);
        }
    }

    @Override
    public void onOccurrencesError(String msg) {
        Console.error(msg);
    }

    @Override
    public void onResultSelected(ResultSelectedEvent event) {
        Console.info( "Selection: " +  event.getSelectedResultItem().getStId() );
        InDiagramOccurrencesFactory.searchForInstanceInDiagram(event.getSelectedResultItem().getStId(), args.getDiagramStId(), this);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        args = event.getSearchArguments();
        this.clear();
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }
    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-DetailsInfoPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/search/infopanel/SelectionInfoPanel.css";

        String container();
    }
}
