package org.reactome.web.diagram.search.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchPerformedEvent;
import org.reactome.web.diagram.search.SearchPerformedHandler;
import org.reactome.web.diagram.search.events.AutoCompleteRequestedEvent;
import org.reactome.web.diagram.search.events.PanelCollapsedEvent;
import org.reactome.web.diagram.search.events.PanelExpandedEvent;
import org.reactome.web.diagram.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.results.scopebar.ScopeBarPanel;
import org.reactome.web.diagram.util.Console;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultsPanel extends AbstractAccordionPanel implements ScopeBarPanel.Handler,
        SearchPerformedHandler, AutoCompleteRequestedHandler {
    private EventBus eventBus;

    private DeckLayoutPanel content;
    private ScopeBarPanel scopeBar;
    private List<ResultsWidget> resultsWidgets = new ArrayList<>();
    private ResultsWidget activeResultWidget;

    private SearchArguments searchArguments;

    public ResultsPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.sinkEvents(Event.ONCLICK);

        resultsWidgets.add(new InDiagramSearchPanel(eventBus));
        resultsWidgets.add(new OtherDiagramSearchPanel());
        resultsWidgets.add(new InSelectionSearchPanel());

        scopeBar = new ScopeBarPanel(this);
        scopeBar.addButton("This diagram", ScopeBarPanel.RESOURCES.scopeLocal());
        scopeBar.addButton("Other diagrams", ScopeBarPanel.RESOURCES.scopeGlobal());
        scopeBar.addButton("In selection", ScopeBarPanel.RESOURCES.scopeTarget());

        content = new DeckLayoutPanel();
        content.setStyleName(RESOURCES.getCSS().content());
        resultsWidgets.forEach(w -> content.add(w.asWidget()));
        setActiveResultsWidget(0);
        content.setAnimationVertical(false);
        content.setAnimationDuration(500);

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().main());
        main.add(scopeBar);
        main.add(content);
        add(main);

        show(false);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler){
        return this.addHandler(handler, ClickEvent.getType());
    }

    public void addResultSelectedHandler(ResultSelectedHandler handler) {
        resultsWidgets.forEach(resultsWidget -> resultsWidget.addResultSelectedHandler(handler));
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        show(false);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        searchArguments = event.getSearchArguments();
        boolean isValid = searchArguments.hasValidTerm();
        show(isValid);
        if(isValid) {

            scopeBar.setResultsNumber(0, 99);
            scopeBar.setResultsNumber(1, 99);
            scopeBar.setResultsNumber(2, 0);

            updateResult();
        }
    }

    @Override
    public void onScopeChanged(int selected) {
        Console.info("   >>>> Scope changed to " + selected);
        setActiveResultsWidget(selected);
        updateResult();
    }

    @Override
    public void onPanelCollapsed(PanelCollapsedEvent event) {
        super.onPanelCollapsed(event);
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        show(searchArguments.hasValidTerm());
    }

    private void updateResult() {
//        Console.info(" >>> Updating for " + searchArguments.getTerm());
        activeResultWidget.updateResults(searchArguments);
    }

    private void setActiveResultsWidget(int index) {
        activeResultWidget = resultsWidgets.get(index);
        if (activeResultWidget != null) {
            content.showWidget(index);
        }
    }

    private void show(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
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
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ResultsPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/results/ResultsPanel.css";

        String main();

        String content();

    }

    static CellListResource CUSTOM_LIST_STYLE;
    static {
        CUSTOM_LIST_STYLE = GWT.create(CellListResource.class);
        CUSTOM_LIST_STYLE.cellListStyle().ensureInjected();
    }

    public interface CellListResource extends CellList.Resources {

        @CssResource.ImportedWithPrefix("diagram-CellListResource")
        interface CustomCellList extends CellList.Style {
            String CSS = "org/reactome/web/diagram/search/results/ResultsList.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(CustomCellList.CSS)
        CustomCellList cellListStyle();
    }
}
