package org.reactome.web.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.controls.navigation.NavigationControlPanel;
import org.reactome.web.diagram.controls.settings.HideableContainerPanel;
import org.reactome.web.diagram.controls.settings.RightContainerPanel;
import org.reactome.web.diagram.controls.top.LeftTopLauncherPanel;
import org.reactome.web.diagram.controls.top.RightTopLauncherPanel;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.legends.*;
import org.reactome.web.diagram.messages.AnalysisMessage;
import org.reactome.web.diagram.messages.ErrorMessage;
import org.reactome.web.diagram.messages.LoadingMessage;
import org.reactome.web.diagram.renderers.common.HoveredItem;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;
import static org.reactome.web.diagram.data.content.Content.Type.SVG;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ViewerContainer extends AbsolutePanel implements RequiresResize,
        ContentLoadedHandler, ContentRequestedHandler,
        GraphObjectSelectedHandler,
        LayoutLoadedHandler,
        CanvasExportRequestedHandler, ControlActionHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler {

    private EventBus eventBus;
    private Context context;

    private Map<Content.Type, Visualiser> visualisers;
    private Visualiser activeVisualiser;

    private IllustrationPanel illustration;
    private Anchor watermark;

    private String flagTerm;

    public ViewerContainer(EventBus eventBus) {
        this.getElement().addClassName("pwp-ViewerContainer");
        this.eventBus = eventBus;

        visualisers = new HashMap<>();

        initialise();
        initHandlers();
    }


    protected void initialise() {
        //All Viewers with their thumbnails
        visualisers.put(DIAGRAM, new DiagramVisualiser());
        visualisers.put(SVG, new DiagramVisualiser());

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

        //Launcher panels
        this.add(new LeftTopLauncherPanel(eventBus));
        this.add(new RightTopLauncherPanel(eventBus));

        //Settings panel
        rightContainerPanel.add(new HideableContainerPanel(eventBus));

        //Illustration panel
        this.add(this.illustration = new IllustrationPanel(), 0 , 0);
    }

    public void highlightGraphObject(GraphObject graphObject) {
        activeVisualiser.highlightGraphObject(graphObject);
    }

    public void highlightInteractor(DiagramInteractor diagramInteractor) {
        activeVisualiser.highlightInteractor(diagramInteractor);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        context = event.getContext();
        setWatermarkVisible(true);
        setWatermarkURL(context, null, this.flagTerm);
        setActiveVisualiser(context);
        activeVisualiser.contentLoaded(context);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        setWatermarkVisible(false);
        this.resetIllustration();
        activeVisualiser.contentRequested(); //TODO maybe iterate all visualisers and content request
    }

    @Override
    public void onDiagramExportRequested(CanvasExportRequestedEvent event) {
        activeVisualiser.exportView();
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        setWatermarkURL(context, activeVisualiser.getSelected(), this.flagTerm = event.getTerm());
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        setWatermarkURL(context, activeVisualiser.getSelected(), this.flagTerm = null);
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        double zoomDelta = UserActionsManager.ZOOM_DELTA;
        switch (event.getAction()) {
            case FIT_ALL:       activeVisualiser.fitDiagram(true);          break;
            case ZOOM_IN:       activeVisualiser.zoomDelta(zoomDelta);      break;
            case ZOOM_OUT:      activeVisualiser.zoomDelta(-zoomDelta);     break;
            case UP:            activeVisualiser.padding(0, 10);            break;
            case RIGHT:         activeVisualiser.padding(-10, 0);           break;
            case DOWN:          activeVisualiser.padding(0, -10);           break;
            case LEFT:          activeVisualiser.padding(10, 0);            break;
            case FIREWORKS:     overview();                                 break;
        }
    }

    @Override
    public void onGraphObjectSelected(final GraphObjectSelectedEvent event) {
        setWatermarkURL(this.context, event.getGraphObject(), flagTerm);
        selectItem(event.getGraphObject()); //TODO check this...
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        context = event.getContext();
        setActiveVisualiser(context);
        activeVisualiser.layoutLoaded(context);
    }

    @Override
    public void onResize() {
        //TODO
    }

    public void selectItem(GraphObject item) {
        resetIllustration();
        if (item != null) {
            activeVisualiser.setSelection(new HoveredItem(item), true, false);
        } else {
            activeVisualiser.resetSelection();
        }
    }

    public void setIllustration(String url){
        this.illustration.setUrl(url);
    }

    public void resetHighlight() {
        activeVisualiser.resetHighlight();
    }

    public void resetIllustration(){
        if(this.illustration!=null) {
            this.illustration.reset();
        }
    }

    private void setWatermarkURL(Context context, GraphObject selection, String flag) {
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
                if (flag != null && !flag.isEmpty()) {
                    href.append("&FLG=").append(flag);
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
//        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
//        eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
//        eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
//        eventBus.addHandler(AnalysisResetEvent.TYPE, this);
//        eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);

        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
//        eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);

//        eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
//        eventBus.addHandler(IllustrationSelectedEvent.TYPE, this);
//
//        eventBus.addHandler(InteractorsCollapsedEvent.TYPE, this);
//        eventBus.addHandler(InteractorHoveredEvent.TYPE, this);
//        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
//        eventBus.addHandler(InteractorsLayoutUpdatedEvent.TYPE, this);
//        eventBus.addHandler(InteractorsFilteredEvent.TYPE, this);
//        eventBus.addHandler(InteractorSelectedEvent.TYPE, this);
//        eventBus.addHandler(InteractorProfileChangedEvent.TYPE, this);
//
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
//        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
//        eventBus.addHandler(ThumbnailAreaMovedEvent.TYPE, this);
        eventBus.addHandler(ControlActionEvent.TYPE, this);

//        eventBus.addHandler(StructureImageLoadedEvent.TYPE, this);
    }

    private void overview(){
        eventBus.fireEvent(new FireworksOpenedEvent(context.getContent().getDbId()));
    }

    private void setActiveVisualiser(Context context){
        if(context != null) {
            Visualiser visualiser = visualisers.get(context.getContent().getType());
            if (visualiser != null) {
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
