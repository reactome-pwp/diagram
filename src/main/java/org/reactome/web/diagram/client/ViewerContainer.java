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
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.events.FireworksOpenedEvent;
import org.reactome.web.diagram.handlers.CanvasExportRequestedHandler;
import org.reactome.web.diagram.handlers.ControlActionHandler;
import org.reactome.web.diagram.legends.*;
import org.reactome.web.diagram.messages.AnalysisMessage;
import org.reactome.web.diagram.messages.ErrorMessage;
import org.reactome.web.diagram.messages.LoadingMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ViewerContainer extends AbsolutePanel implements RequiresResize,
        CanvasExportRequestedHandler, ControlActionHandler {
    private EventBus eventBus;
    private Context context;

    private List<Visualiser> visualisers;
    private Visualiser activeVisualiser;

    private IllustrationPanel illustration;
    private Anchor watermark;

    public ViewerContainer(EventBus eventBus) {
        this.getElement().addClassName("pwp-ViewerContainer");
        this.eventBus = eventBus;

        visualisers = new LinkedList<>();

        initHandlers();
    }

    public void flagItems() {
        setWatermarkURL(context, layoutManager.getSelected(), this.flagTerm = event.getTerm());
        layoutManager.setFlagged(event.getFlaggedItems());
        activeVisualiser.flag(event.getFlaggedItems(), this.context);
    }

    protected void initialise() {
        //All Viewers with their thumbnails
        for (Visualiser visualiser : visualisers) {
            add(visualiser);
        }

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

    @Override
    public void onDiagramExportRequested(CanvasExportRequestedEvent event) {
        activeVisualiser.exportView();
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
            case FIREWORKS:     overview();                             break;
        }
    }

    @Override
    public void onResize() {

    }

    public void setIllustration(String url){
        this.illustration.setUrl(url);
    }

    public void resetIllustration(){
        if(this.illustration!=null) {
            this.illustration.reset();
        }
    }

    public void setWatermarkURL(Context context, GraphObject selection, String flag) {
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

    public void setWatermarkVisible(boolean visible){
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

//        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
//        eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);

//        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
//        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
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
//        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
//        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
//        eventBus.addHandler(ThumbnailAreaMovedEvent.TYPE, this);
        eventBus.addHandler(ControlActionEvent.TYPE, this);

//        eventBus.addHandler(StructureImageLoadedEvent.TYPE, this);
    }

    private void overview(){
        eventBus.fireEvent(new FireworksOpenedEvent(context.getContent().getDbId()));
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
