package org.reactome.web.diagram.search.suggester;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.controls.top.search.SearchPerformedEvent;
import org.reactome.web.diagram.controls.top.search.SearchPerformedHandler;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.SuggestionResetEvent;
import org.reactome.web.diagram.search.events.SuggestionSelectedEvent;
import org.reactome.web.diagram.search.handlers.SuggestionResetHandler;
import org.reactome.web.diagram.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.searchbox.SearchBoxArrowKeysEvent;
import org.reactome.web.diagram.search.searchbox.SearchBoxArrowKeysHandler;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionPanel extends AbstractAccordionPanel implements SearchPerformedHandler, SearchBoxArrowKeysHandler,
        SelectionChangeEvent.Handler, SuggestionResetHandler {
    private EventBus eventBus;
    private final SingleSelectionModel<SearchResultObject> selectionModel;
    private CellList<SearchResultObject> suggestions;
    private ListDataProvider<SearchResultObject> dataProvider;

    /**
     * The key provider that provides the unique ID of a DatabaseObject.
     */
    public static final ProvidesKey<SearchResultObject> KEY_PROVIDER = item -> {
        if(item == null){
            return null;
        }else if(item instanceof GraphObject) {
            GraphObject graphObject = (GraphObject) item;
            return graphObject.getDbId();
        } else if( item instanceof InteractorSearchResult) {
            InteractorSearchResult interactorSearchResult = (InteractorSearchResult) item;
            return interactorSearchResult.getAccession();
        }
        return null;
    };

    public SuggestionPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.sinkEvents(Event.ONCLICK);

        // Add a selection model so we can select cells.
        selectionModel = new SingleSelectionModel<>(KEY_PROVIDER);
        selectionModel.addSelectionChangeHandler(this);

        SuggestionCell suggestionCell = new SuggestionCell();

        suggestions = new CellList<>(suggestionCell, KEY_PROVIDER);
        suggestions.sinkEvents(Event.FOCUSEVENTS);
        suggestions.setSelectionModel(selectionModel);

        suggestions.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        suggestions.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(this.suggestions);

        this.add(suggestions);
        //Setting the legend style
        setStyleName(RESOURCES.getCSS().suggestionPanel());
    }

    public HandlerRegistration addClickHandler(ClickHandler handler){
        return this.addHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addSuggestionSelectedHandler(SuggestionSelectedHandler handler) {
        return addHandler(handler, SuggestionSelectedEvent.TYPE);
    }

    @Override
    public void onArrowKeysPressed(SearchBoxArrowKeysEvent event) {
        if(suggestions.getRowCount()>0){
            SearchResultObject current = selectionModel.getSelectedObject();
            int currentIndex = current==null?-1:dataProvider.getList().indexOf(current);
            int toIndex = currentIndex;
            if(event.getValue() == KeyCodes.KEY_DOWN) {
                toIndex = currentIndex + 1 < dataProvider.getList().size() ? currentIndex + 1 : dataProvider.getList().size() - 1;
            }else if(event.getValue() == KeyCodes.KEY_UP) {
                toIndex = currentIndex - 1 > 0 ? currentIndex - 1 : 0;
            }
            if(toIndex!=-1 && toIndex!=currentIndex) {
                SearchResultObject newSelection = dataProvider.getList().get(toIndex);
                suggestions.getRowElement(toIndex).scrollIntoView();
                selectionModel.setSelected(newSelection, true);
            }
        }
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        SearchResultObject sel = selectionModel.getSelectedObject();
        List<SearchResultObject> searchResult = event.getSuggestions();
        if(!searchResult.isEmpty() && !searchResult.contains(sel)) {
            selectionModel.clear();
        } else if (searchResult.isEmpty() && !event.getTerm().isEmpty()){
            suggestions.setEmptyListWidget(new HTML("No results found for '" + event.getTerm() +"'"));
        } else {
            suggestions.setEmptyListWidget(null);
        }

        dataProvider.getList().clear();
        dataProvider.getList().addAll(searchResult);
        suggestions.setVisibleRange(0, searchResult.size()); //configure list paging
        suggestions.setRowCount(searchResult.size());

        if (dataProvider.getList().isEmpty()) {
            fireEvent(new SuggestionSelectedEvent(null));
        }else{
            fireEvent(new SuggestionSelectedEvent(selectionModel.getSelectedObject()));
        }
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent event) {
        SearchResultObject obj = selectionModel.getSelectedObject();
        if (obj!=null) {
            if (obj instanceof GraphObject) {
                eventBus.fireEventFromSource(new GraphObjectSelectedEvent((GraphObject) obj, true), this);
            }
        }
        fireEvent(new SuggestionSelectedEvent(obj));
    }

    @Override
    public void onSuggestionReset(SuggestionResetEvent event) {
        selectionModel.clear();
    }


    public static SuggestionResources RESOURCES;
    static {
        RESOURCES = GWT.create(SuggestionResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SuggestionResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SuggestionPanelCSS.CSS)
        SuggestionPanelCSS getCSS();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SuggestionPanel")
    public interface SuggestionPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/suggester/SuggestionPanel.css";

        String suggestionPanel();
    }

}
