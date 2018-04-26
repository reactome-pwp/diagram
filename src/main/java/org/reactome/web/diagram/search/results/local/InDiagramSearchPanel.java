package org.reactome.web.diagram.search.results.local;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.FacetsLoadedEvent;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.ResultsPanel;
import org.reactome.web.diagram.search.results.ResultsWidget;
import org.reactome.web.diagram.search.results.cells.ResultItemCell;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.scroller.client.InfiniteScrollList;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("all")
public class InDiagramSearchPanel extends Composite implements ResultsWidget, SelectionChangeEvent.Handler,
        ContentLoadedHandler, ContentRequestedHandler {

    public final static String PREFIX = DiagramFactory.SERVER + "/ContentService/search/diagram/";

    private EventBus eventBus;
    private Context context;

    private SearchArguments arguments;
    private SearchResultObject selectedItem;

    private SingleSelectionModel<SearchResultObject> selectionModel = new SingleSelectionModel<>(ResultsPanel.KEY_PROVIDER);
    private InfiniteScrollList<SearchResultObject> resultsList;
    private InDiagramProvider dataProvider;

    private List<FacetContainer> facets;

    private FlowPanel main;

    public InDiagramSearchPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        ResultItemCell cell = new ResultItemCell();
        dataProvider = new InDiagramProvider();
        resultsList = new InfiniteScrollList(cell, ResultsPanel.KEY_PROVIDER, dataProvider, ResultsPanel.CUSTOM_LIST_STYLE);
        resultsList.setSelectionModel(selectionModel);

        main = new FlowPanel();
        main.add(resultsList);

        selectionModel.addSelectionChangeHandler(this);

        initWidget(main);
        /*
        SuggestionCell suggestionCell = new SuggestionCell();

        suggestions = new CellList<>(suggestionCell, KEY_PROVIDER);
        suggestions.sinkEvents(Event.FOCUSEVENTS);
        suggestions.setSelectionModel(selectionModel);

        suggestions.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        suggestions.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        */

        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }

    public HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        return addHandler(handler, FacetsLoadedEvent.TYPE);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.context = null;
        this.selectedItem = null;
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
    }

    @Override
    public void updateResults(SearchArguments args, boolean clearSelection) {
        if (clearSelection) {
            selectionModel.clear();
            selectedItem = null;
        }

        if(arguments == null || !arguments.equals(args)) {
            arguments = args;

            dataProvider.setSearchArguments(args, PREFIX);

            //Include the results of the search in the interactors (by searching inside the graph)
            dataProvider.setExtraItemsToShow(findInDiagramInteractors(args));

            resultsList.setPageSize(30);
            resultsList.loadFirstPage();
        }
        restoreSelection();
        fireEvent(new FacetsLoadedEvent(facets, args.getFacets()));
    }

    @Override
    public void setFacets(List<FacetContainer> facets) {
        this.facets = facets;
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent event) {
        selectedItem = selectionModel.getSelectedObject();
        fireEvent(new ResultSelectedEvent(selectedItem));
    }

    private List<SearchResultObject> findInDiagramInteractors(SearchArguments args) {
        List<SearchResultObject> rtn = new ArrayList<>();
        if(context != null) {
            for (InteractorSearchResult obj : context.getInteractors().getInteractorSearchResult(LoaderManager.INTERACTORS_RESOURCE, context.getContent())) {
                if (obj.containsTerm(args.getQuery())) {
                    obj.setSearchDisplay(args.getHighlightingExpression());
                    rtn.add(obj);
                }
            }
        }
        Console.info(">>>>> Interactors found: " + rtn.size());
        return rtn.isEmpty() ? null : rtn;
    }

    @Override
    public void suspendSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(null));
        }
    }

    private void restoreSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(selectedItem));
        }
    }

}
