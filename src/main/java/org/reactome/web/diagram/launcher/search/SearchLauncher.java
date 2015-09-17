package org.reactome.web.diagram.launcher.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;
import org.reactome.web.diagram.search.events.PanelCollapsedEvent;
import org.reactome.web.diagram.search.events.PanelExpandedEvent;
import org.reactome.web.diagram.search.handlers.PanelCollapsedHandler;
import org.reactome.web.diagram.search.handlers.PanelExpandedHandler;
import org.reactome.web.diagram.search.provider.SuggestionsProvider;
import org.reactome.web.diagram.search.provider.SuggestionsProviderImpl;
import org.reactome.web.diagram.search.searchbox.*;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchLauncher extends AbsolutePanel implements ClickHandler,
        DiagramLoadedHandler, DiagramRequestedHandler, LayoutLoadedHandler, SearchBoxUpdatedHandler,
        SearchBoxArrowKeysHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String OPENING_TEXT = "Search for any diagram term ...";

    private EventBus eventBus;
    private SuggestionsProvider<GraphObject> suggestionsProvider;

    private SearchBox input = null;
    private PwpButton searchBtn = null;

    private Boolean isExpanded = false;

    private Timer focusTimer;

    public SearchLauncher(EventBus eventBus) {
        //Setting the search style
        setStyleName(RESOURCES.getCSS().launchPanel());

        this.eventBus = eventBus;

        this.searchBtn = new PwpButton("Search in the diagram", RESOURCES.getCSS().launch(), this);
        this.add(searchBtn);

        this.input = new SearchBox();
        this.input.setStyleName(RESOURCES.getCSS().input());
        this.input.getElement().setPropertyString("placeholder", OPENING_TEXT);
        this.add(input);

        focusTimer = new Timer() {
            @Override
            public void run() {
                SearchLauncher.this.input.setFocus(true);
            }
        };

        this.initHandlers();
        this.searchBtn.setEnabled(false);
    }

    public HandlerRegistration addSearchBoxArrowKeysHandler(SearchBoxArrowKeysHandler handler){
        return input.addSearchBoxArrowKeysHandler(handler);
    }

    public HandlerRegistration addPanelCollapsedHandler(PanelCollapsedHandler handler){
        return addHandler(handler, PanelCollapsedEvent.TYPE);
    }

    public HandlerRegistration addPanelExpandedHandler(PanelExpandedHandler handler){
        return addHandler(handler, PanelExpandedEvent.TYPE);
    }

    public HandlerRegistration addSearchPerformedHandler(SearchPerformedHandler handler){
        return addHandler(handler, SearchPerformedEvent.TYPE);
    }

    @Override
    public void onArrowKeysPressed(SearchBoxArrowKeysEvent event) {
        if(event.getValue() == KeyCodes.KEY_ESCAPE) {
            setFocus(false);
            this.collapsePanel();
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(this.searchBtn)){
            if(!isExpanded){
                expandPanel();
            }else{
                collapsePanel();
            }
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.input.setValue(""); // Clear searchbox value and fire the proper event
        this.collapsePanel();
        this.suggestionsProvider = null;
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.searchBtn.setEnabled(true);
        DiagramContent content = event.getContext().getContent();
        this.suggestionsProvider = new SuggestionsProviderImpl(content);
    }

    @Override
    public void onSearchUpdated(SearchBoxUpdatedEvent event) {
        if(suggestionsProvider!=null) {
            List<GraphObject> suggestions = suggestionsProvider.getSuggestions(input.getText().trim());
            fireEvent(new SearchPerformedEvent(suggestions));
        }
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.searchBtn.setEnabled(false);
    }

    public void setFocus(boolean focused){
        this.input.setFocus(focused);
    }

    private void collapsePanel(){
        if(focusTimer.isRunning()){
            focusTimer.cancel();
        }
        removeStyleName(RESOURCES.getCSS().launchPanelExpanded());
        input.removeStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = false;
        fireEvent(new PanelCollapsedEvent());
    }

    private void expandPanel(){
        addStyleName(RESOURCES.getCSS().launchPanelExpanded());
        input.addStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = true;
        fireEvent(new PanelExpandedEvent());
        focusTimer.schedule(300);
    }

    private void initHandlers(){
        this.input.addSearchBoxUpdatedHandler(this);
        this.input.addSearchBoxArrowKeysHandler(this);

        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }


    public static SearchLauncherResources RESOURCES;
    static {
        RESOURCES = GWT.create(SearchLauncherResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SearchLauncherResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SearchLauncherCSS.CSS)
        SearchLauncherCSS getCSS();

        @Source("images/search_clicked.png")
        ImageResource launchClicked();

        @Source("images/search_disabled.png")
        ImageResource launchDisabled();

        @Source("images/search_hovered.png")
        ImageResource launchHovered();

        @Source("images/search_normal.png")
        ImageResource launchNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SearchLauncher")
    public interface SearchLauncherCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/launcher/search/SearchLauncher.css";

        String launchPanel();

        String launchPanelExpanded();

        String launch();

        String input();

        String inputActive();
    }

}
