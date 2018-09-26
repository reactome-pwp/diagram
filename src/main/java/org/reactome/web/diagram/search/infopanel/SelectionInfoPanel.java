package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.SuggestionSelectedEvent;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Deprecated
public class SelectionInfoPanel extends AbstractAccordionPanel implements SuggestionSelectedHandler,
        ContentRequestedHandler, ContentLoadedHandler {
    private EventBus eventBus;
    private Context context;

    public SelectionInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.setStyleName(RESOURCES.getCSS().container());

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            context = event.getContext();
        }
    }

    @Override
    public void onSuggestionSelected(SuggestionSelectedEvent event) {
        this.clear();

        SearchResultObject obj = event.getSearchResultObject();
        if(obj!=null) {
            if(obj instanceof InteractorSearchResult) {
                this.add(new InteractorInfoPanel(eventBus, (InteractorSearchResult) obj));
            } else if(obj instanceof GraphObject) {
                this.add(new GraphObjectInfoPanel(eventBus, (GraphObject) obj, context));
            }
        }
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-SelectionInfoPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/search/infopanel/SelectionInfoPanel.css";

        String container();
    }
}
