package org.reactome.web.diagram.search.results.global;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.client.DiagramFactory;
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
import org.reactome.web.scroller.client.InfiniteScrollList;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class OtherDiagramSearchPanel extends Composite implements ResultsWidget, SelectionChangeEvent.Handler {

    public final static String PREFIX = DiagramFactory.SERVER + "/ContentService/search/fireworks/";


    private SearchArguments arguments;
    private SearchResultObject selectedItem;

    private SingleSelectionModel<SearchResultObject> selectionModel = new SingleSelectionModel<>(ResultsPanel.KEY_PROVIDER);
    private InfiniteScrollList<SearchResultObject> resultsList;
    private OtherDiagramProvider dataProvider;

    private List<FacetContainer> facets;

    private FlowPanel main;

    public OtherDiagramSearchPanel() {

        ResultItemCell cell = new ResultItemCell();
        dataProvider = new OtherDiagramProvider();
        resultsList = new InfiniteScrollList(cell, ResultsPanel.KEY_PROVIDER, dataProvider, ResultsPanel.CUSTOM_LIST_STYLE);
        resultsList.setSelectionModel(selectionModel);

        main = new FlowPanel();
        main.add(resultsList);

        selectionModel.addSelectionChangeHandler(this);

        initWidget(main);
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }

    public HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        return addHandler(handler, FacetsLoadedEvent.TYPE);
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
