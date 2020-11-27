package org.reactome.web.diagram.thumbnail.diagram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.client.StaticIllustrationPanel;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Event;
import org.reactome.web.pwp.model.client.classes.Figure;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

public class StaticIllustrationThumbnail extends FlowPanel implements ContentRequestedHandler, ContentLoadedHandler, GraphObjectSelectedHandler {

    public static final int THUMBNAIL_RESIZE_THRESHOLD_1 = 1000;
    public static final int DIAGRAM_THUMBNAIL_MAX_WIDTH = 155;
    private static final int DEFAULT_WIDTH = 130;
    private static final int DEFAULT_HEIGHT = 75;
    private static final int DEFAULT_VIEWPORT_W = THUMBNAIL_RESIZE_THRESHOLD_1 + 300;
    public static final double FACTOR_06 = 0.6;
    private boolean isLegendPanelVisible = false;
    private double viewportWidth;
    private int diagramThumbnailsWidth;

    private EventBus eventBus;
    private Context context;

    private Long loadeddbId;
    private FlowPanel mainStaticIllustrationFlowPanel;
    private FlowPanel selectStaticIllustrationFlowPanel;

    private StaticIllustrationPanel staticIllustrationPanel;
    private OMSVGSVGElement svg;

    // TODO set them to NULL once a diagarm changed
    private String diagramIllustrationURL = null;
    private boolean diagramFigureLoadingInProgress = false;

    private String selectionIllustrationURL = null;
    private boolean selectionFigureLoadingInProgress = false;

    public StaticIllustrationThumbnail(EventBus eventBus) {
        this.getElement().addClassName("pwp-StaticIllustrationThumbnail");
        this.eventBus = eventBus;
        this.staticIllustrationPanel = new StaticIllustrationPanel();

        initHandlers();
        resize(DEFAULT_VIEWPORT_W);
        this.setStyle();
    }

    private void initHandlers() {
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
    }

    public void diagramRendered(Context context, int diagramThumbnailWidth) {
        if (context == null) return;

        // Analysis, Interactors or Flagging.
        // Whenever one is showing,  we apply factor to the thumbnails resize
        int interactors = (context.getContent() == null ? 0 : context.getContent().getNumberOfBurstEntities());
        isLegendPanelVisible = context.getAnalysisStatus() != null  || context.getFlagTerm() != null || interactors > 0;

        // some cases the thumbnail is too wide leaving very little space for the static thumbnail
        // the idea is to use the thumbnail as part of the correction while applying the FACTOR
        resize(this.viewportWidth, diagramThumbnailWidth);
    }

    public void resize(double viewportWidth, int diagramThumbnailsWidth) {
        this.viewportWidth = viewportWidth;
        this.diagramThumbnailsWidth = diagramThumbnailsWidth;

        int fW = (int) Math.round(DEFAULT_WIDTH * getFactor());
        int fH = (int) Math.round(DEFAULT_HEIGHT * getFactor());

        // adjusting flexing position in case it's too small
        Element parent = this.getElement().getParentElement();
        if (parent != null && parent.getStyle() != null) {
            if (this.viewportWidth <= 800) parent.getStyle().setProperty("alignItems", "unset");
            else parent.getStyle().setProperty("alignItems", "center");
        }

        this.setWidth(fW + "px");
        this.setHeight(fH + "px");
    }

    private double getFactor() {
        double factorRet = 1.0;

        if (!isLegendPanelVisible) return factorRet;

        int correction = (diagramThumbnailsWidth >= DIAGRAM_THUMBNAIL_MAX_WIDTH) ? 80 : 0;
        if (viewportWidth <= THUMBNAIL_RESIZE_THRESHOLD_1 - correction) {
            factorRet = FACTOR_06;
        }

        return factorRet;
    }

    /**
     * It will be called at DiagramCanvas.setSize()
     * when viewport threshold is below certain limit.
     *
     * @param viewportWidth note: don't confuse it with width to resize.
     */
    public void resize(double viewportWidth) {
        resize(viewportWidth, 0);
    }

    public void addDiagramFigureToThumbnails() {
        this.loadeddbId = this.context.getContent().getDbId();

        resetAllStaticIllustration();

        diagramFigureLoadingInProgress = true;
        ContentClient.query(loadeddbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                diagramFigureLoadingInProgress = false;
                if (databaseObject instanceof Event) {
                    final Event event = (Event) databaseObject;
                    for (Figure figure : event.getFigure()) {
                        diagramIllustrationURL = DiagramFactory.ILLUSTRATION_SERVER + figure.getUrl();
                        if (selectionIllustrationURL == null && !selectionFigureLoadingInProgress) {
                            createMainStaticIllustrationFlowPanel(databaseObject, diagramIllustrationURL);
                        }
                    }
                }
            }

            @Override
            public void onContentClientException(ContentClientHandler.Type type, String message) {
                diagramFigureLoadingInProgress = false;
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                diagramFigureLoadingInProgress = false;
            }
        });

    }

    public void addSelectionFigureToThumbnails(GraphObject selection) {
        Long dbId = selection.getDbId();

        resetStaticIllustrationSelection();

        selectionFigureLoadingInProgress = true;
        ContentClient.query(dbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                selectionFigureLoadingInProgress = false;
                if (databaseObject instanceof Event) {
                    final Event event = (Event) databaseObject;
                    for (Figure figure : event.getFigure()) {
                        selectionIllustrationURL = DiagramFactory.ILLUSTRATION_SERVER + figure.getUrl();
                        if (diagramIllustrationURL == null && !diagramFigureLoadingInProgress) {
                            createSelectStaticIllustration(databaseObject, selectionIllustrationURL);
                        }
                    }
                }
            }

            @Override
            public void onContentClientException(ContentClientHandler.Type type, String message) {
                selectionFigureLoadingInProgress = false;
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                selectionFigureLoadingInProgress = false;
            }
        });
    }

    public void createMainStaticIllustrationFlowPanel(final DatabaseObject databaseObject, final String url) {
        if (url == null || url.isEmpty()) return;

        showStaticThumbnail();

        mainStaticIllustrationFlowPanel = new FlowPanel();
        mainStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());

        Image image = new Image(url);
        image.setUrl(url);
        image.setTitle("Illustration for " + databaseObject.getDisplayName());
        image.setAltText("Illustration for " + databaseObject.getDisplayName());
        image.addClickHandler(clickEvent -> {
            staticIllustrationPanel.setStaticIllustrationUrl(url);
            staticIllustrationPanel.toggle();
        });
        mainStaticIllustrationFlowPanel.add(image);
        mainStaticIllustrationFlowPanel.setVisible(true);

        add(mainStaticIllustrationFlowPanel);
    }

    public void createSelectStaticIllustration(final DatabaseObject databaseObject, final String url) {
        if (url == null || url.isEmpty()) return;

        showStaticThumbnail();

        selectStaticIllustrationFlowPanel = new FlowPanel();
        selectStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());
        selectStaticIllustrationFlowPanel.addStyleName(RESOURCES.getCSS().selectedStaticThumbnails());
        this.getElement().addClassName(RESOURCES.getCSS().selected());

        Image image = new Image(url);
        image.setUrl(url);
        image.setTitle("Illustration for " + databaseObject.getDisplayName());
        image.setAltText("Illustration for " + databaseObject.getDisplayName());
        image.addClickHandler(clickEvent -> {
            staticIllustrationPanel.setStaticIllustrationUrl(url);
            staticIllustrationPanel.toggle();
        });

        selectStaticIllustrationFlowPanel.add(image);
        selectStaticIllustrationFlowPanel.setVisible(true);

        add(selectStaticIllustrationFlowPanel);
    }

    private void setStyle() {
        Style style = this.getElement().getStyle();

        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setBottom(0, Style.Unit.PX);
        style.setMarginLeft(5, Style.Unit.PX);
        style.setProperty("boxSizing", "unset");

        // Invisible by default
        style.setDisplay(Style.Display.NONE);
    }

    private void showStaticThumbnail() {
        this.setVisible(true);
    }

    private void hideStaticThumbnail() {
        if ((mainStaticIllustrationFlowPanel != null && !mainStaticIllustrationFlowPanel.isVisible()) &&
                (selectStaticIllustrationFlowPanel != null && !selectStaticIllustrationFlowPanel.isVisible())) return;

        this.setVisible(false);
    }

    public void resetStaticIllustrationSelection() {
        if (staticIllustrationPanel != null) {
            staticIllustrationPanel.reset();
            staticIllustrationPanel.clear();
        }

        diagramIllustrationURL = null;
        diagramFigureLoadingInProgress = false;
        selectionIllustrationURL = null;
        selectionFigureLoadingInProgress = false;

        if (selectStaticIllustrationFlowPanel != null) {
            this.getElement().removeClassName(RESOURCES.getCSS().selected());
            remove(selectStaticIllustrationFlowPanel);
        }
    }

    public void resetAllStaticIllustration() {
        resetStaticIllustrationSelection();
        if (mainStaticIllustrationFlowPanel != null ) remove(mainStaticIllustrationFlowPanel);
        hideStaticThumbnail();
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
        if (event.getContext().getContent().getType() == DIAGRAM) {
//            Scheduler.get().scheduleDeferred(this::addDiagramFigureToThumbnails);
            addDiagramFigureToThumbnails();
        }
    }

    public StaticIllustrationPanel getStaticIllustrationPanel() {
        return staticIllustrationPanel;
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        resetAllStaticIllustration();
        this.context = null;
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        if (selectStaticIllustrationFlowPanel !=null) {
            this.getElement().removeClassName(RESOURCES.getCSS().selected());
            remove(selectStaticIllustrationFlowPanel);
        }

        if (event.getGraphObject() != null && event.getGraphObject() instanceof GraphEvent) {
            addSelectionFigureToThumbnails(event.getGraphObject());
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
    }

    @CssResource.ImportedWithPrefix("diagram-StaticIllustrationThumbnail")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/thumbnail/StaticIllustrationThumbnail.css";

        String mainStaticThumbnails();

        String selectedStaticThumbnails();

        String selected();

    }
}
