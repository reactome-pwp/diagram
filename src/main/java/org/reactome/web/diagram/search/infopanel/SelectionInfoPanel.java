package org.reactome.web.diagram.search.infopanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.SuggestionSelectedEvent;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SelectionInfoPanel extends AbstractAccordionPanel implements SuggestionSelectedHandler,
        DiagramLoadedHandler, DiagramLoadRequestHandler {
    private EventBus eventBus;
    private DiagramContext context;

    public SelectionInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.setStyleName(RESOURCES.getCSS().container());

        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
    }


    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        context = null;
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
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
