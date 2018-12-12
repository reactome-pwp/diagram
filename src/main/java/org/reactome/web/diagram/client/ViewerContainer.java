package org.reactome.web.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.controls.navigation.NavigationControlPanel;
import org.reactome.web.diagram.controls.notifications.NotificationsContainer;
import org.reactome.web.diagram.controls.settings.HideableContainerPanel;
import org.reactome.web.diagram.controls.settings.RightContainerPanel;
import org.reactome.web.diagram.controls.top.LeftTopLauncherPanel;
import org.reactome.web.diagram.controls.top.RightTopLauncherPanel;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.legends.*;
import org.reactome.web.diagram.messages.AnalysisMessage;
import org.reactome.web.diagram.messages.ErrorMessage;
import org.reactome.web.diagram.messages.LoadingMessage;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;
import static org.reactome.web.diagram.data.content.Content.Type.SVG;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ViewerContainer extends AbsolutePanel implements RequiresResize,
        CanvasExportRequestedHandler, ControlActionHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler, DiagramObjectsFlagRequestHandler,
        GraphObjectSelectedHandler, IllustrationSelectedHandler {

    private EventBus eventBus;
    private Context context;

    private Map<Content.Type, Visualiser> visualisers;
    private Visualiser activeVisualiser;

    private IllustrationPanel illustration;
    private Anchor watermark;

    public static Timer windowScrolling = new Timer() {
        @Override
        public void run() { /* Nothing here */ }
    };

    public ViewerContainer(EventBus eventBus) {
        this.getElement().setClassName("pwp-ViewerContainer");
        this.eventBus = eventBus;

        visualisers = new HashMap<>();

        initialise();
        initHandlers();
    }


    protected void initialise() {
        //All Viewers with their thumbnails
        visualisers.put(DIAGRAM, new DiagramVisualiser(eventBus));
        visualisers.put(SVG, new SVGVisualiser(eventBus));

        for (Visualiser vis: visualisers.values()) {
            this.add(vis);
        }

        //Set this as default
        activeVisualiser = visualisers.get(DIAGRAM);

        //DO NOT CHANGE THE ORDER OF THE FOLLOWING TWO LINES
        this.add(new LoadingMessage(eventBus));                 //Loading message panel
        this.add(new AnalysisMessage(eventBus));                //Analysis overlay message panel
        this.add(new ErrorMessage(eventBus));                   //Error message panel

        //Watermark
        this.addWatermark();

        //Right container
        RightContainerPanel rightContainerPanel = new RightContainerPanel();
        this.add(rightContainerPanel);

        //Control panel
        this.add(new NavigationControlPanel(eventBus));

        //Bottom Controls container
        BottomContainerPanel bottomContainerPanel = new BottomContainerPanel();
        this.add(bottomContainerPanel);

        //Flagged Objects control panel
        bottomContainerPanel.add(new FlaggedItemsControl(eventBus));

        //Enrichment legend and control panels
        rightContainerPanel.add(new EnrichmentLegend(eventBus));
        bottomContainerPanel.add(new EnrichmentControl(eventBus));

        //Expression legend and control panels
        rightContainerPanel.add(new ExpressionLegend(eventBus));
        bottomContainerPanel.add(new ExpressionControl(eventBus));

        //Interactors control panel
        bottomContainerPanel.add(new InteractorsControl(eventBus));

        //Info panel
        if (DiagramFactory.SHOW_INFO) {
            this.add(new DiagramInfo(eventBus));
        }

        //Notifications Panel
        this.add(new NotificationsContainer(eventBus));

        //Launcher panels
        this.add(new LeftTopLauncherPanel(eventBus));
        this.add(new RightTopLauncherPanel(eventBus));


        //Settings panel
        rightContainerPanel.add(new HideableContainerPanel(eventBus));

        //Illustration panel
        this.add(this.illustration = new IllustrationPanel(), 0 , 0);
    }

    public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
        return activeVisualiser.highlightGraphObject(graphObject, notify);
    }

    public void highlightInteractor(DiagramInteractor diagramInteractor) {
        activeVisualiser.highlightInteractor(diagramInteractor);
    }

    public void contentLoaded(final Context context) {
        this.context = context;
        setWatermarkVisible(true);
        setWatermarkURL(context, null);
        setActiveVisualiser(context);
        activeVisualiser.contentLoaded(context);
    }

    public void contentRequested() {
        activeVisualiser.resetSelection(false);
        activeVisualiser.resetHighlight(false);
        activeVisualiser.contentRequested();
        resetIllustration();
        setWatermarkVisible(false);
        context = null;
    }

    public void expressionColumnChanged() {
        activeVisualiser.expressionColumnChanged();
    }

    public void interactorsCollapsed(String resource) {
        activeVisualiser.interactorsCollapsed(resource);
    }

    public void interactorsFiltered() {
        activeVisualiser.interactorsFiltered();
    }

    public void interactorsLayoutUpdated(){
        activeVisualiser.interactorsLayoutUpdated();
    }

    public void interactorsLoaded(){
        activeVisualiser.interactorsLoaded();
    }

    public void interactorsResourceChanged(OverlayResource resource) {
        activeVisualiser.interactorsResourceChanged(resource);
    }

    @Override
    public void onCanvasExportRequested(CanvasExportRequestedEvent event) {
        activeVisualiser.exportView();
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        context.setFlagTerm(null);
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.resetFlag();
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        context.setFlagTerm(event.getTerm());
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.flagItems(event.getFlaggedItems());
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        context.setFlagTerm(null);
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.resetFlag();
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        switch (event.getAction()) {
            case FIT_ALL:       activeVisualiser.fitDiagram(true);  break;
            case ZOOM_IN:       activeVisualiser.zoomIn();                    break;
            case ZOOM_OUT:      activeVisualiser.zoomOut();                   break;
            case UP:            activeVisualiser.padding(0, 10);     break;
            case RIGHT:         activeVisualiser.padding(-10, 0);    break;
            case DOWN:          activeVisualiser.padding(0, -10);    break;
            case LEFT:          activeVisualiser.padding(10, 0);     break;
            case FIREWORKS:     overview();                                   break;
        }
    }

    @Override
    public void onGraphObjectSelected(final GraphObjectSelectedEvent event) {
        setWatermarkURL(this.context, event.getGraphObject());
//        selectItem(event.getGraphObject()); //TODO check this...
    }

    @Override
    public void onIllustrationSelected(IllustrationSelectedEvent event) {
        this.setIllustration(event.getUrl());
    }

    @Override
    public void onResize() {
        final int width = this.getOffsetWidth();
        final int height = this.getOffsetHeight();
        activeVisualiser.setSize(width, height);

        //Deffer the resizing of the rest
        Scheduler.get().scheduleDeferred(() -> {
            visualisers.values().stream()
                    .filter(v -> v!=activeVisualiser)
                    .forEach(v -> v.setSize(width, height));
        });
    }

    public boolean selectItem(GraphObject item, boolean notify) {
        resetIllustration();
        return activeVisualiser.selectGraphObject(item, notify);
    }

    public void setIllustration(String url){
        this.illustration.setUrl(url);
    }

    public boolean resetHighlight(boolean notify) {
        return activeVisualiser.resetHighlight(notify);
    }

    public boolean resetSelection(boolean notify) {
        return activeVisualiser.resetSelection(notify);
    }

    public void resetIllustration(){
        if(this.illustration!=null) {
            this.illustration.reset();
        }
    }

    public void resetAnalysis() {
        setWatermarkURL(this.context, activeVisualiser.getSelected());
        activeVisualiser.resetAnalysis();
    }

    public void loadAnalysis() {
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.loadAnalysis();
    }

    public void resetContext() {
        this.context = null;
        activeVisualiser.resetFlag();
        activeVisualiser.resetContext();
    }

    public void setContext(final Context context) {
        this.context = context;
        setActiveVisualiser(context);
        activeVisualiser.setContext(context);
        activeVisualiser.fitDiagram(false);
    }

    private void setWatermarkURL(Context context, GraphObject selection) {
        if(watermark!=null) {
            StringBuilder href = new StringBuilder(DiagramFactory.WATERMARK_BASE_URL);
            String pathwayStId = context == null ? null : context.getContent().getStableId();
            if (pathwayStId != null && !pathwayStId.isEmpty()) {
                href.append("#/").append(pathwayStId);
                if (selection != null) {
                    if (selection.getStId() != null && !selection.getStId().isEmpty()) {
                        href.append("&SEL=").append(selection.getStId());
                    }
                }
                AnalysisStatus analysisStatus = context.getAnalysisStatus();
                if (analysisStatus != null) {
                    href.append("&DTAB=AN").append("&ANALYSIS=").append(analysisStatus.getToken()).append("&RESOURCE=").append(analysisStatus.getResource());
                }
                if (context.getFlagTerm() != null && !context.getFlagTerm().isEmpty()) {
                    href.append("&FLG=").append(context.getFlagTerm());
                }
            }
            watermark.setHref(href.toString());
        }
    }

    private void setWatermarkVisible(boolean visible){
        if(watermark!=null) {
            watermark.setVisible(visible);
        }
    }

    private void addWatermark(){
        if(DiagramFactory.WATERMARK) {
            Image img = new Image(RESOURCES.logo());
            SafeHtml image = SafeHtmlUtils.fromSafeConstant(img.toString());
            watermark = new Anchor(image, DiagramFactory.WATERMARK_BASE_URL, "_blank");
            watermark.setTitle("Open this pathway in Reactome Pathway Browser");
            watermark.setStyleName(RESOURCES.getCSS().watermark());
            watermark.setVisible(false);
            add(watermark);
        }
    }

    private void initHandlers() {
        //Only add the window scroll handler if it makes sense
        if(DiagramFactory.SCROLL_SENSITIVITY > 0) {
            Window.addWindowScrollHandler(event -> windowScrolling.schedule(DiagramFactory.SCROLL_SENSITIVITY));
        }

        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);

        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);

        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);

        eventBus.addHandler(IllustrationSelectedEvent.TYPE, this);

        eventBus.addHandler(ControlActionEvent.TYPE, this);
    }

    private void overview(){
        eventBus.fireEventFromSource(new FireworksOpenedEvent(context.getContent().getDbId()), this);
    }

    private void setActiveVisualiser(Context context){
        if(context != null) {
            Visualiser visualiser = visualisers.get(context.getContent().getType());
            if (visualiser != null && activeVisualiser != visualiser) {
                for (Visualiser vis : visualisers.values()) {
                    if(vis == visualiser) {
                        vis.asWidget().setVisible(true);
                    } else {
                        vis.asWidget().setVisible(false);
                    }
                }
                activeVisualiser = visualiser;
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

        @Source("images/watermark.png")
        ImageResource logo();
    }

    @CssResource.ImportedWithPrefix("diagram-ViewerContainer")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/client/ViewerContainer.css";

        String watermark();

    }
}
