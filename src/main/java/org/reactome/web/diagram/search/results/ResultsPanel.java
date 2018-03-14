package org.reactome.web.diagram.search.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.diagram.search.SearchLauncher;
import org.reactome.web.diagram.search.SearchPerformedEvent;
import org.reactome.web.diagram.search.SearchPerformedHandler;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.AutoCompleteRequestedEvent;
import org.reactome.web.diagram.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.util.Console;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultsPanel extends AbstractAccordionPanel implements ScopeBarPanel.Handler,
        SearchPerformedHandler, AutoCompleteRequestedHandler{
    private EventBus eventBus;
//    private final SingleSelectionModel<SearchResultObject> selectionModel;
    private CellList<SearchResultObject> suggestions;
    private ListDataProvider<SearchResultObject> dataProvider;

    private DeckLayoutPanel content;

    public ResultsPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.sinkEvents(Event.ONCLICK);

        ScopeBarPanel scopeBar = new ScopeBarPanel(this);
        scopeBar.addButton("This diagram", SearchLauncher.RESOURCES.options());
        scopeBar.addButton("Other diagrams", SearchLauncher.RESOURCES.options());
        scopeBar.addButton("In selection", SearchLauncher.RESOURCES.options());

        content = new DeckLayoutPanel();
        content.setStyleName(RESOURCES.getCSS().content());
        content.add(new Label("First tab"));
        content.add(new Label("Second tab"));
        content.add(new Label("Third tab"));
        content.showWidget(0);
        content.setAnimationVertical(false);
        content.setAnimationDuration(500);

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().main());
        main.add(scopeBar);
        main.add(content);
        show(false);

        add(main);
    }


    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        show(false);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        String term = event.getTerm();
        if (term == null || term.isEmpty()) return;
//        show(true);
    }

    @Override
    public void onScopeChanged(int selected) {
        Console.info("   >>>> Scope changed to " + selected);
        content.showWidget(selected);
    }

//    private Widget getScopeBar() {
//        FlowPanel scopeBarPanel = new FlowPanel();
//        scopeBarPanel.setStyleName(RESOURCES.getCSS().scopeBarPanel());
//        scopeBarPanel.add(this.molecules = getButton("Molecules", RESOURCES.molecules()));
//        scopeBarPanel.add(this.pathways = getButton("Pathways", RESOURCES.pathways()));
//        scopeBarPanel.add(this.interactors = getButton("Interactors", RESOURCES.interactors()));
//
//        return scopeBarPanel;
//    }

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
}
