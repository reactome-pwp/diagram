package org.reactome.web.diagram.search.autocomplete;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.search.AutoCompleteRequestedEvent;
import org.reactome.web.diagram.search.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.SearchPerformedEvent;
import org.reactome.web.diagram.search.SearchPerformedHandler;
import org.reactome.web.diagram.search.autocomplete.cells.AutoCompleteCell;
import org.reactome.web.diagram.search.autocomplete.cells.RecentSearchCell;
import org.reactome.web.diagram.search.events.OptionsCollapsedEvent;
import org.reactome.web.diagram.search.events.OptionsExpandedEvent;
import org.reactome.web.diagram.search.handlers.OptionsCollapsedHandler;
import org.reactome.web.diagram.search.handlers.OptionsExpandedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.util.Console;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AutoCompletePanel extends AbstractAccordionPanel implements SearchPerformedHandler,
        OptionsExpandedHandler, OptionsCollapsedHandler,
        AutoCompleteRequestedHandler, AutoCompleteResultsFactory.AutoCompleteResultsHandler {

    private final static int AUTOCOMPLETE_SIZE = 9;
    private final static int RECENT_SIZE = 9;

    private CellList<AutoCompleteResult> autoCompleteList;
    private CellList<String> recentItemsList;

    private Widget recentSearchesPanel;

    public AutoCompletePanel() {
        setStyleName(RESOURCES.getCSS().container());

        FlowPanel main = new FlowPanel();
        add(main);

        // Create a cell to render each value.
        AutoCompleteCell autoCompleteCell = new AutoCompleteCell();
        autoCompleteList = new CellList<>(autoCompleteCell);
        autoCompleteList.setStyleName(RESOURCES.getCSS().autoCompleteList());
        autoCompleteList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        final SingleSelectionModel<AutoCompleteResult> selectionModel = new SingleSelectionModel<>();
        autoCompleteList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(event -> {
            AutoCompleteResult selected = selectionModel.getSelectedObject();
            Console.info("new Selection: " + selected.getResult());
        });

        // Set the total row count. This isn't strictly necessary, but it affects
        // paging calculations, so its good habit to keep the row count up to date.
        autoCompleteList.setRowCount(AUTOCOMPLETE_SIZE, true);
        main.add(autoCompleteList);

        recentSearchesPanel = getRecentSearchesPanel();
        showRecentSearches(false);
        main.add(recentSearchesPanel);
    }


    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        requestAutoCompleteResults(event.getTerm());
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        getElement().getStyle().setDisplay(Style.Display.NONE);
        RecentSearchesManager.get().insert(event.getTerm());
    }

    public void requestAutoCompleteResults(String tag) {
        if (tag != null && !tag.isEmpty()) {
            AutoCompleteResultsFactory.searchForTag(tag, this);
        } else {
            clearAutoCompleteList();
        }
    }

    @Override
    public void onOptionsCollapsed(OptionsCollapsedEvent event) {
        getElement().getStyle().setDisplay(Style.Display.INLINE);
    }

    @Override
    public void onOptionsExpanded(OptionsExpandedEvent event) {
        getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void onAutoCompleteSearchResult(List<AutoCompleteResult> results) {
        getElement().getStyle().setDisplay(Style.Display.INLINE);
        autoCompleteList.setRowData(results);
        List<String> items = RecentSearchesManager.get().getRecentItems();
        if (!items.isEmpty()) {
            recentItemsList.setRowData(items);
            showRecentSearches(true);
        }
    }

    @Override
    public void onAutoCompleteError() {
        Console.error("Error retrieving autocomplete suggestions"); //TODO treat this
    }


    private Widget getRecentSearchesPanel() {
        Label title = new Label("Recent searches");
        title.setStyleName(RESOURCES.getCSS().dividerTitle());

        FlowPanel divider = new FlowPanel();
        divider.setStyleName(RESOURCES.getCSS().divider());
        divider.add(title);

        RecentSearchCell recentSearchCell = new RecentSearchCell();
        recentItemsList = new CellList<>(recentSearchCell);
        recentItemsList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        recentItemsList.setStyleName(RESOURCES.getCSS().recentSearchesList());
        recentItemsList.setRowCount(RECENT_SIZE, true);

        FlowPanel panel = new FlowPanel();
        panel.add(divider);
        panel.add(recentItemsList);
        return panel;
    }

    private void clearAutoCompleteList() {
        autoCompleteList.setRowCount(0);
    }

    private void showRecentSearches(boolean visible) {
        if (visible) {
            recentSearchesPanel.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            recentSearchesPanel.getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/search_suggestion.png")
        ImageResource searchSuggestion();

        @Source("../images/search_recent.png")
        ImageResource searchRecent();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-AutoCompletePanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/autocomplete/AutoCompletePanel.css";

        String container();

        String icon();

        String autoCompleteList();

        String divider();

        String dividerTitle();

        String recentSearchesList();

    }
}
