package org.reactome.web.diagram.search.results;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class OtherDiagramSearchPanel extends Composite implements ResultsWidget {

    private Label lb;

    public OtherDiagramSearchPanel() {
        lb = new Label();
                /*
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
        */
                initWidget(lb);
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }


    @Override
    public void updateResults(SearchArguments args) {
        lb.setText("Other diagrams search: " + args.getTerm());
    }
}
