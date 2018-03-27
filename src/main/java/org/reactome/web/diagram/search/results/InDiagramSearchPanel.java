package org.reactome.web.diagram.search.results;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.cells.ResultItemCell;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.scroller.client.InfiniteScrollList;

import static org.reactome.web.scroller.client.util.Placeholder.ROWS;
import static org.reactome.web.scroller.client.util.Placeholder.START;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("all")
public class InDiagramSearchPanel extends Composite implements ResultsWidget, SelectionChangeEvent.Handler,
        ResultItemCell.Handler {

    public final static String PREFIX = DiagramFactory.SERVER + "/ContentService/search/diagram/";

    private EventBus eventBus;
    private SearchArguments arguments;
    private ResultItem selectedItem;

    private SingleSelectionModel<ResultItem> selectionModel = new SingleSelectionModel<>(ResultItem.KEY_PROVIDER);
    private InfiniteScrollList<ResultItem> resultsList;
    private InDiagramProvider dataProvider;

    private FlowPanel main;

    public InDiagramSearchPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        main = new FlowPanel();

        ResultItemCell cell = new ResultItemCell(this);
        dataProvider = new InDiagramProvider();
        resultsList = new InfiniteScrollList(cell, ResultItem.KEY_PROVIDER, dataProvider, ResultsPanel.CUSTOM_LIST_STYLE);
        resultsList.setSelectionModel(selectionModel);
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
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }

    @Override
    public void updateResults(SearchArguments args) {
        if(arguments == null || !arguments.equals(args)) {
            arguments = args;
            //TODO use a Stringbuilder
            dataProvider.setURL(PREFIX + args.getDiagramStId() + "?query=" + args.getTerm() + "&" + START.getUrlValue() + "&" + ROWS.getUrlValue());
            resultsList.setPageSize(30);
            resultsList.loadFirstPage();
        }
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent event) {
        selectedItem = selectionModel.getSelectedObject();
//        Console.info("Selection changed to: " + selectedItem.getPrimarySearchDisplay());
        fireEvent(new ResultSelectedEvent(selectedItem));
    }

    @Override
    public void onFlagChanged(String flag) {
        Console.info("Flagging item : " + flag);
    }

}
