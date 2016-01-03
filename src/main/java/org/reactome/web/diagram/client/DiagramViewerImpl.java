package org.reactome.web.diagram.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.reactome.web.diagram.common.DiagramAnimationHandler;
import org.reactome.web.diagram.common.DisplayManager;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.DiagramStatus;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.graph.model.GraphEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.loader.AnalysisDataLoader;
import org.reactome.web.diagram.data.loader.AnalysisTokenValidator;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.util.DiagramEventBus;
import org.reactome.web.diagram.util.ViewportUtils;
import org.reactome.web.diagram.util.actions.UserActionsHandlers;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.util.LruCache;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
class DiagramViewerImpl extends ResizeComposite implements DiagramViewer, UserActionsHandlers,
        LayoutLoadedHandler, GraphLoadedHandler, InteractorsLoadedHandler, DiagramLoadRequestHandler, ControlActionHandler, ThumbnailAreaMovedHandler,
        InteractorsResourceChangedHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler, ExpressionColumnChangedHandler,
        DiagramAnimationHandler, DiagramProfileChangedHandler, AnalysisProfileChangedHandler,
        GraphObjectHoveredHandler, GraphObjectSelectedHandler, DiagramLoadedHandler, CanvasExportRequestedHandler,
        DiagramObjectsFlagRequestHandler, DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler,
        IllustrationSelectedHandler {

    private static final double ZOOM_FACTOR = 0.025;
    private static final double ZOOM_DELTA = 0.25;
    private static final double ZOOM_TOUCH_DELTA = 200;

    private static final int DIAGRAM_CONTEXT_CACHE_SIZE = 5;
    private final DiagramCanvas canvas; //Canvas only created once and reused every time a new diagram is loaded
    private final DiagramManager diagramManager;
    // mouse positions relative to canvas (not the model)
    // Do not assign the same value at the beginning
    Coordinate mouseCurrent = CoordinateFactory.get(-100, -100);
    Coordinate mousePrevious = CoordinateFactory.get(-200, -200);
    Coordinate mouseDown = null;
    private LruCache<String, DiagramContext> contextMap;
    private EventBus eventBus;
    private DiagramContext context;
    private LoaderManager loaderManager;
    private int viewportWidth = 0;
    private int viewportHeight = 0;

    private HoveredItem hovered = null;
    private GraphObject selected = null;
    private Set<DiagramObject> halo = new HashSet<>();
    private Set<DiagramObject> flagged = new HashSet<>();

    private boolean diagramMoved = false;
    private boolean forceDraw = false;
    private Double fingerDistance;

    private String flagTerm;
    private AnalysisStatus analysisStatus;

    public DiagramViewerImpl() {
        this.eventBus = new DiagramEventBus();
        this.canvas = new DiagramCanvas(this.eventBus);
        this.contextMap = new LruCache<>(DIAGRAM_CONTEXT_CACHE_SIZE);
        this.loaderManager = new LoaderManager(this.eventBus);
        AnalysisDataLoader.initialise(this.eventBus);

        this.diagramManager = new DiagramManager(new DisplayManager(this));
        this.initWidget(this.canvas);
        this.getElement().addClassName("pwp-DiagramViewer"); //IMPORTANT!
    }

    private void initialise() {
        this.initHandlers();
        this.viewportWidth = getOffsetWidth();
        this.viewportHeight = getOffsetHeight();
        AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                doUpdate();
                AnimationScheduler.get().requestAnimationFrame(this); // Call it again.
            }
        });
    }

    private void initHandlers() {
        this.canvas.addUserActionsHandlers(this);

        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);

        this.eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);

        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);
        this.eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);

        this.eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(IllustrationSelectedEvent.TYPE, this);

        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);

        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        this.eventBus.addHandler(GraphLoadedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        this.eventBus.addHandler(ThumbnailAreaMovedEvent.TYPE, this);
        this.eventBus.addHandler(ControlActionEvent.TYPE, this);
    }

    private void doUpdate() {
        this.doUpdate(false);
    }

    private void doUpdate(boolean force) {
        if (this.forceDraw) {
            this.forceDraw = false;
            this.draw();
            this.drawInteractors();
            return;
        }
        if (force || !mouseCurrent.equals(mousePrevious)) {
            if (this.context != null) {
                HoveredItem hovered = this.getHovered(mouseCurrent);
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                this.canvas.setCursor(item == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
                this.highlight(hovered);
                this.mousePrevious = this.mouseCurrent;
            }
        }
    }

    private void draw() {
        if (context == null) return;
        long start = System.currentTimeMillis();
        List<DiagramObject> selected = this.selected != null ? this.selected.getDiagramObjects() : new LinkedList<DiagramObject>();
        canvas.clear();
        Collection<DiagramObject> items = context.getVisibleElements(viewportWidth, viewportHeight);
        canvas.render(items, context);
        canvas.select(selected, context);
        canvas.highlight(hovered, context);
        canvas.decorators(hovered, context);
        canvas.halo(halo, context);
        canvas.flag(flagged, context);
        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        long time = System.currentTimeMillis() - start;
        this.eventBus.fireEventFromSource(new DiagramRenderedEvent(context.getContent(), visibleArea, items.size(), time), this);
    }

    private void drawInteractors() {
        // A list of visible interactors is needed
    }

    @Override
    public HandlerRegistration addAnalysisResetHandler(AnalysisResetHandler handler) {
        return this.addHandler(handler, AnalysisResetEvent.TYPE);
    }

    @Override
    public HandlerRegistration addCanvasNotSupportedEventHandler(CanvasNotSupportedHandler handler) {
        return this.eventBus.addHandler(CanvasNotSupportedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addDatabaseObjectSelectedHandler(GraphObjectSelectedHandler handler) {
        return this.addHandler(handler, GraphObjectSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDatabaseObjectHoveredHandler(GraphObjectHoveredHandler handler) {
        return this.addHandler(handler, GraphObjectHoveredEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramLoadedHandler(DiagramLoadedHandler handler) {
        return this.addHandler(handler, DiagramLoadedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramObjectsFlaggedHandler(DiagramObjectsFlaggedHandler handler) {
        return this.addHandler(handler, DiagramObjectsFlaggedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addFireworksOpenedHandler(FireworksOpenedHandler handler) {
        return this.addHandler(handler, FireworksOpenedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramObjectsFlagResetHandler(DiagramObjectsFlagResetHandler handler) {
        return this.addHandler(handler, DiagramObjectsFlagResetEvent.TYPE);
    }

    @Override
    public void flagItems(String identifier) {
        this.eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier), this);
    }

    @Override
    public DiagramStatus getDiagramStatus() {
        return this.context.getDiagramStatus();
    }

    @Override
    public int getViewportWidth() {
        return this.viewportWidth;
    }

    @Override
    public int getViewportHeight() {
        return this.viewportHeight;
    }

    @Override
    public void transform(Coordinate offset, double factor) {
        //An animation can be working and a new pathway might be requested :(
        if (this.context == null) return;
        DiagramStatus status = this.context.getDiagramStatus();
        status.setOffset(offset);
        status.setFactor(factor);
        this.forceDraw = true;
        Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
        this.eventBus.fireEventFromSource(new DiagramZoomEvent(factor, visibleArea), this);
    }

    @Override
    public void highlightItem(String stableIdentifier) {
        try {
            GraphObject item = this.context.getContent().getDatabaseObject(stableIdentifier);
            highlight(item);
        } catch (Exception e) {/*Nothing here*/}
    }

    @Override
    public void highlightItem(Long dbIdentifier) {
        try {
            GraphObject item = this.context.getContent().getDatabaseObject(dbIdentifier);
            highlight(item);
        } catch (Exception e) {/*Nothing here*/}
    }

    private void highlight(HoveredItem hovered) {
        if (Objects.equals(this.hovered, hovered)) return;
        DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
        GraphObject graphObject = item != null && item.getIsFadeOut() == null ? item.getGraphObject() : null;
        canvas.highlight(hovered, context);
        canvas.decorators(hovered, context);

        if (hovered != null) {
            if (hovered.getAttachment() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getAttachment()), this);
            } else if (hovered.getSummaryItem() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getSummaryItem()), this);
            } else if (hovered.getContextMenuTrigger() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getContextMenuTrigger()), this);
            } else {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item), this);
            }
        }

        //Even though at the level of HoveredItem they are different, we only notify if the hovered diagram
        //object is actually different, so we do not take into account attachments or entities summary.
        DiagramObject prev = this.hovered != null ? this.hovered.getHoveredObject() : null;
        this.hovered = hovered;
        if (!Objects.equals(prev, item)) {
            //we don't rely on the listener of the following event because finer grain of the hovering is lost
            GraphObjectHoveredEvent event = new GraphObjectHoveredEvent(graphObject, item);
            this.eventBus.fireEventFromSource(event, this);
            fireEvent(event); //needs outside notification
        }
    }

    private void highlight(GraphObject graphObject) {
        HoveredItem hovered = new HoveredItem(graphObject);
        if (Objects.equals(this.hovered, hovered)) return;
        this.hovered = hovered;
        canvas.highlight(hovered, context);
        //we don't rely on the listener of the following event because finer grain of the hovering is lost
        GraphObjectHoveredEvent event = new GraphObjectHoveredEvent(graphObject);
        this.eventBus.fireEventFromSource(event, this);
    }

    @Override
    public void loadDiagram(String stId) {
        if (stId != null) {
            if (this.context == null || !stId.equals(this.context.getContent().getStableId())) {
                this.load(stId); //Names are interchangeable because there are symlinks
            }
        }
    }

    @Override
    public void loadDiagram(Long dbId) {
        if (dbId != null) {
            if (this.context == null || !dbId.equals(this.context.getContent().getDbId())) {
                this.load("" + dbId); //Names are interchangeable because there are symlinks
            }
        }
    }

    private void load(String identifier) {
        this.resetSelection();
        this.resetHighlight();
        this.resetDialogs();
        this.resetIllustration();
        this.diagramManager.cancelDisplayAnimation();
        this.eventBus.fireEventFromSource(new DiagramRequestedEvent(), this);
        DiagramContext context = this.contextMap.get(identifier);
        if (context == null) {
            this.resetContext();
            this.loaderManager.load(identifier);
        } else {
            this.setContext(context);
        }
    }

    private void loadAnalysis(AnalysisStatus analysisStatus) {
        if (analysisStatus == null) {
            if (this.analysisStatus != null) {
                this.eventBus.fireEventFromSource(new AnalysisResetEvent(false), this);
            }
        } else if (!analysisStatus.equals(this.context.getAnalysisStatus())) {
            this.analysisStatus = analysisStatus;
            this.context.clearAnalysisOverlay();
            AnalysisDataLoader.get().loadAnalysisResult(analysisStatus, this.context.getContent());
        }
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        switch (event.getAction()) {
            case FIT_ALL:       this.fitDiagram(true);          break;
            case ZOOM_IN:       this.zoomDelta(ZOOM_DELTA);     break;
            case ZOOM_OUT:      this.zoomDelta(-ZOOM_DELTA);    break;
            case UP:            this.padding(0, 10);            break;
            case RIGHT:         this.padding(-10, 0);           break;
            case DOWN:          this.padding(0, -10);           break;
            case LEFT:          this.padding(10, 0);            break;
            case FIREWORKS:     this.overview();                break;
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!this.diagramMoved) {
            this.setSelection(this.getHovered(mouseCurrent), false, true);
        }
        this.diagramMoved = false;
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        HoveredItem hovered = this.getHovered(mouseCurrent);
        DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
        GraphObject toOpen = item != null ? item.getGraphObject() : null;
        if (toOpen instanceof GraphPathway) {
            this.load(toOpen.getDbId().toString());
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.diagramMoved = false;
        int button = event.getNativeEvent().getButton();
        switch (button) {
            case NativeEvent.BUTTON_RIGHT:
                HoveredItem hovered = this.getHovered(mouseCurrent);
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                this.setSelection(hovered, false, true);
                this.context.showDialog(this.eventBus, item, this.canvas);
                break;
            default:
                setMouseDownPosition(event.getRelativeElement(), event);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        setMousePosition(event.getRelativeElement(), event);
        if (mouseDown != null) {
            this.diagramMoved = true;
            this.canvas.setCursor(Style.Cursor.MOVE);
            Coordinate delta = this.mouseCurrent.minus(this.mouseDown);
            this.padding(delta);
            this.setMouseDownPosition(event.getRelativeElement(), event);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        this.diagramMoved = false;
        this.mouseDown = null;
        this.canvas.setCursor(Style.Cursor.DEFAULT);
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.canvas.setCursor(hovered == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
        this.mouseDown = null;
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.stopPropagation();
        event.preventDefault();
        setMousePosition(event.getRelativeElement(), event);

        if (this.context == null) return;

        double factor = this.context.getDiagramStatus().getFactor();
        int delta = event.getDeltaY();
        factor = ViewportUtils.checkFactor(factor - delta * ZOOM_FACTOR);

        this.zoom(factor, this.mouseCurrent);
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        this.mouseDown = null;
        this.diagramMoved = false;
        this.fingerDistance = null;
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        if (!this.diagramMoved) {
            //Do NOT use this.mouseCurrent in the next line
            this.setSelection(this.getHovered(mouseDown), false, true);
        }
        this.mouseDown = null;
        this.diagramMoved = false;
        this.fingerDistance = null;
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        event.stopPropagation();
        event.preventDefault();
        if (mouseDown != null) {
            this.diagramMoved = true;
            //Do NOT use this.mouseCurrent in the next line
            Coordinate mouseCurrent = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
            Coordinate delta = mouseCurrent.minus(this.mouseDown);
            this.padding(delta);
            this.mouseDown = mouseCurrent;
        } else {
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate centre = finger1.add(finger2.minus(finger1).divide(2.0));
            Coordinate delta = finger1.minus(finger2);
            double distance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
            double deltaFactor = (distance - fingerDistance) / ZOOM_TOUCH_DELTA;
            this.fingerDistance = distance;
            double factor = ViewportUtils.checkFactor(this.context.getDiagramStatus().getFactor() + deltaFactor);
            zoom(factor, centre);
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        event.stopPropagation();
        event.preventDefault();
        if (event.getChangedTouches().length() == 1) {
            this.mouseDown = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
        } else {
            this.mouseDown = null;
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate delta = finger1.minus(finger2);
            fingerDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
        }
    }


    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        if (event.getFireExternally()) {
            fireEvent(event);
        }
        this.resetAnalysis();
        this.canvas.setWatermarkURL(this.context, this.selected, this.flagTerm);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        this.analysisStatus.setAnalysisSummary(event.getSummary());
        this.analysisStatus.setExpressionSummary(event.getExpressionSummary());
        this.context.setAnalysisOverlay(analysisStatus, event.getPathwayIdentifiers(), event.getPathwaySummaries());
        this.canvas.setWatermarkURL(this.context, this.selected, this.flagTerm);
        forceDraw = true;
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        this.context.clearAnalysisOverlay();
        this.analysisStatus.setExpressionSummary(null);
        forceDraw = true;
    }

    @Override
    public void onGraphObjectSelected(final GraphObjectSelectedEvent event) {
        GraphObject graphObject = event.getGraphObject();
        if (!Objects.equals(graphObject, this.selected)) {
            if (graphObject == null) {
                this.halo = new HashSet<>();
                this.selected = null;
            } else {
                boolean fadeOut = !graphObject.getDiagramObjects().isEmpty();
                for (DiagramObject diagramObject : graphObject.getDiagramObjects()) {
                    fadeOut &= diagramObject.getIsFadeOut() != null;
                }
                if (!fadeOut) {
                    this.selected = graphObject;
                    this.halo = graphObject.getRelatedDiagramObjects();
                    if (graphObject instanceof GraphPhysicalEntity) {
                        GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                        for (GraphPhysicalEntity parent : pe.getParentLocations()) {
                            this.halo.addAll(parent.getDiagramObjects());   //halo its parents but not the reactions where they participate
                        }
                    }
                }
            }
            this.canvas.setWatermarkURL(this.context, this.selected, this.flagTerm);
            if (event.getZoom()) {
                this.diagramManager.displayDiagramObjects(this.halo);
            }
            if (this.selected != null) this.halo.removeAll(this.selected.getDiagramObjects());
            forceDraw = true;
            if (event.getFireExternally()) {
                fireEvent(event);
            }
        }
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        //In order to have fine grain hovering capabilities, this class is not taking actions for onGraphObjectHovered
        //when it is fired by its own, so we ONLY want to do the STANDARD action (highlight) when the event comes from
        //the outside. That is the reason of the next line of code
        if (event.getSource().equals(this)) return;
        if (context != null) {
            //this.hovered = new HoveredItem(event.getHoveredObjects()); // Don't do it here. Hovering can also be fired from the outside
            canvas.highlight(new HoveredItem(event.getGraphObject()), this.context);
        }
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                Coordinate model = context.getDiagramStatus().getModelCoordinate(mouseCurrent);
                DiagramObject hovered = DiagramViewerImpl.this.hovered == null ? null : DiagramViewerImpl.this.hovered.getDiagramObjects().get(0);
                canvas.notifyHoveredExpression(hovered, model);
                forceDraw = true; //We give priority to other listeners here
            }
        });
    }

    @Override
    public void onDiagramExportRequested(CanvasExportRequestedEvent event) {
        if (context != null) {
            this.canvas.exportImage(this.context.getContent().getStableId());
        }
    }


    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        this.canvas.setWatermarkURL(context, this.selected, this.flagTerm = event.getTerm());
        this.flagged = event.getFlaggedItems();
        this.canvas.flag(this.flagged, this.context);
        if (event.getNotify()) {
            this.fireEvent(event);
        }
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        String term = event.getTerm();
        Set<GraphObject> items = this.context.getContent().getIdentifierMap().getElements(term);
        Set<DiagramObject> flagged = new HashSet<>();
        if (items != null) {
            for (GraphObject item : items) {
                flagged.addAll(item.getDiagramObjects());
                if (item instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) item;
                    for (GraphPhysicalEntity entity : pe.getParentLocations()) {
                        flagged.addAll(entity.getDiagramObjects());
                    }
                }
            }
        }
        boolean notify = !event.getSource().equals(this);
        this.eventBus.fireEventFromSource(new DiagramObjectsFlaggedEvent(term, flagged, notify), this);
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        this.canvas.setWatermarkURL(context, this.selected, this.flagTerm = null);
        if (this.flagged != null) {
            this.flagged = new HashSet<>();
            this.canvas.flag(this.flagged, this.context);
            if (!event.getSource().equals(this)) {
                this.fireEvent(event);
            }
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.canvas.setWatermarkVisible(true);
        this.canvas.setWatermarkURL(event.getContext(), null, this.flagTerm);
        fireEvent(event);
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        this.canvas.setWatermarkVisible(false);
        final Pathway diagram = event.getPathway();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                loadDiagram(diagram.getDbId());
            }
        });
    }

    @Override
    public void onGraphLoaded(GraphLoadedEvent event) {
        this.eventBus.fireEventFromSource(new DiagramLoadedEvent(context), this);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        context.getContent().clearInteractors();
        forceDraw = true;
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {

        for (RawInteractorEntity interactor : event.getInteractors().getEntities()) {
            context.getContent().setInteractors(interactor.getAcc(), interactor.getCount());
        }
        forceDraw = true;
    }

    @Override
    public void onIllustrationSelected(IllustrationSelectedEvent event) {
        this.canvas.setIllustration(event.getUrl());
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setContext(event.getContext());
        this.fitDiagram(false);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initialise();
            }
        });
    }

    @Override
    public void onProfileChanged(DiagramProfileChangedEvent event) {
        forceDraw = true;
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        forceDraw = true;
    }

    @Override
    public void onResize() {
        super.onResize(); //Need to call super to propagate the resizing to the contained elements
        this.viewportWidth = getOffsetWidth();
        this.viewportHeight = getOffsetHeight();
        this.forceDraw = true;

        if (this.context != null) {
            Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
            this.eventBus.fireEventFromSource(new ViewportResizedEvent(getOffsetWidth(), getOffsetHeight(), visibleArea), this);
        }
    }

    @Override
    public void onThumbnailAreaMoved(ThumbnailAreaMovedEvent event) {
        this.padding(event.getCoordinate().multiply(context.getDiagramStatus().getFactor()));
    }

    @Override
    public void resetAnalysis() {
        this.analysisStatus = null;
        this.context.clearAnalysisOverlay();
        forceDraw = true;
    }

    public void resetDialogs() {
        if (this.context != null) {
            this.context.hideDialogs();
        }
    }

    public void resetIllustration() {
        this.canvas.resetIllustration();
    }

    @Override
    public void resetFlaggedItems() {
        this.eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
    }

    @Override
    public void resetHighlight() {
        if (hovered != null) {
            hovered = null;
            canvas.highlight(null, context);
            eventBus.fireEventFromSource(new GraphObjectHoveredEvent(), this);
        }
    }

    @Override
    public void resetSelection() {
        this.halo = new HashSet<>();
        if (this.selected != null) {
            this.selected = null;
            this.forceDraw = true;
            this.eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false), this);
        }
    }

    @Override
    public void selectItem(String stableIdentifier) {
        selectItem(this.context.getContent().getDatabaseObject(stableIdentifier));
    }

    @Override
    public void selectItem(Long dbIdentifier) {
        selectItem(this.context.getContent().getDatabaseObject(dbIdentifier));
    }

    private void selectItem(GraphObject item) {
        if (item != null) {
            this.setSelection(new HoveredItem(item), true, false);
        } else {
            this.resetSelection();
        }
    }

    @Override
    public void setAnalysisToken(String token, String resource) {
        final AnalysisStatus analysisStatus = (token == null) ? null : new AnalysisStatus(eventBus, token, resource);
        AnalysisTokenValidator.checkTokenAvailability(token, new AnalysisTokenValidator.TokenAvailabilityHandler() {
            @Override
            public void onTokenAvailabilityChecked(boolean available, String message) {
                if (available) {
                    loadAnalysis(analysisStatus);
                } else {
                    eventBus.fireEventFromSource(new DiagramInternalErrorEvent(message), DiagramViewerImpl.this);
                }
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) onResize();
        if (context != null) {
            if (visible) {
                context.restoreDialogs();
            } else {
                context.hideDialogs();
            }
        }
    }

    protected void setMouseDownPosition(Element element, MouseEvent event) {
        this.mouseDown = CoordinateFactory.get(event.getRelativeX(element), event.getRelativeY(element));
    }

    protected void setMousePosition(Element element, MouseEvent event) {
        this.mouseCurrent = CoordinateFactory.get(event.getRelativeX(element), event.getRelativeY(element));
    }

    protected Coordinate getTouchCoordinate(Touch touch) {
        int x = touch.getRelativeX(canvas.getElement());
        int y = touch.getRelativeY(canvas.getElement());
        return CoordinateFactory.get(x, y);
    }

    private HoveredItem getHovered(Coordinate mouse) {
        Coordinate model = context.getDiagramStatus().getModelCoordinate(mouse);
        Collection<DiagramObject> target = this.context.getHoveredTarget(model);
        Collection<HoveredItem> hoveredItems = this.canvas.getHovered(target, model);
        for (HoveredItem hovered : hoveredItems) {
            DiagramObject item = context.getContent().getDiagramObject(hovered.getDiagramId());
            hovered.setDiagramObject(item); //VERY IMPORTANT! Here we have access to the content and can transform diagramId to diagramObject

            //TODO: The graph has to be pruned in the server side
            if (item == null || item.getIsFadeOut() != null) continue;

            if (item.getGraphObject() instanceof GraphPhysicalEntity || item.getGraphObject() instanceof GraphEvent) {
                this.notifyHoveredExpression(item, model);
                return hovered;
            }
        }
        this.notifyHoveredExpression(null, model);
        return null;
    }

    //Before notifying is good practise to check whether there is expression overlay or not
    private void notifyHoveredExpression(DiagramObject item, Coordinate model) {
        if (context.getAnalysisStatus() != null) {
            AnalysisType type = context.getAnalysisStatus().getAnalysisType();
            if (type.equals(AnalysisType.EXPRESSION)) {
                //The reason why the notification is delegated to the canvas is because it keeps track of the
                //expression changes already, so this do not need to be done here.
                this.canvas.notifyHoveredExpression(item, model);
            }
        }
    }

    private void resetContext() {
        this.canvas.clear();
        this.canvas.clearThumbnail();
        if (this.context != null) {
            //this.resetAnalysis(); !IMPORTANT! Do not use this method here
            //Once a context is due to be replaced, the analysis overlay has to be cleaned up
            this.context.clearAnalysisOverlay();
            this.context = null;
        }
        GraphObjectFactory.content = null;
    }

    private void setContext(final DiagramContext context) {
        this.resetContext();

        this.context = context;
        contextMap.put(context.getContent().getStableId(), context);
        GraphObjectFactory.content = context.getContent();

        this.hovered = null;

        this.forceDraw = true;
        if (this.context.getContent().isGraphLoaded()) {
            this.loadAnalysis(this.analysisStatus); //IMPORTANT: This needs to be done once context is been set up above
            this.eventBus.fireEventFromSource(new DiagramLoadedEvent(context), this);
        }
        this.context.restoreDialogs();
    }

    private void setSelection(HoveredItem hoveredItem, boolean zoom, boolean fireExternally) {
        GraphObject toSelect = hoveredItem != null ? hoveredItem.getGraphObject() : null;
        if (toSelect != null) {
            if (hoveredItem.getAttachment() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getAttachment()), this);
            }
            if (hoveredItem.getSummaryItem() != null) {
                Boolean pressed = hovered.getSummaryItem().getPressed();
                hovered.getSummaryItem().setPressed(pressed == null || !pressed);
                forceDraw = true;
                this.eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getSummaryItem()), this);
            }
            if (hoveredItem.getContextMenuTrigger() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getContextMenuTrigger()), this);
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                this.context.showDialog(this.eventBus, item, this.canvas);
            }
        }
        if (!Objects.equals(this.selected, toSelect)) {
            this.resetIllustration();
            this.eventBus.fireEventFromSource(new GraphObjectSelectedEvent(toSelect, zoom, fireExternally), this);
        }
    }

    private void fitDiagram(boolean animation) {
        this.diagramManager.fitDiagram(this.context.getContent(), animation);
    }

    private void overview() {
        fireEvent(new FireworksOpenedEvent(this.context.getContent().getDbId()));
    }

    private void padding(int dX, int dY) {
        padding(CoordinateFactory.get(dX, dY));
    }

    private void padding(Coordinate delta) {
        this.context.getDiagramStatus().padding(delta);
        this.forceDraw = true;
        Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
        this.eventBus.fireEventFromSource(new DiagramPanningEvent(visibleArea), this);
    }

    private void zoomDelta(double deltaFactor) {
        zoom(this.context.getDiagramStatus().getFactor() + deltaFactor);
    }

    private void zoom(double factor) {
        Coordinate viewportCentre = CoordinateFactory.get(this.viewportWidth / 2, this.viewportHeight / 2);
        factor = ViewportUtils.checkFactor(factor);
        zoom(factor, viewportCentre);
    }

    private void zoom(double factor, Coordinate mouse) {
        DiagramStatus status = this.context.getDiagramStatus();
        if (factor == status.getFactor()) return;

        //current and new model positions are used to calculate the delta in order to perform
        //padding to the result of the zooming
        Coordinate current_model = status.getModelCoordinate(mouse);
        status.setFactor(factor);
        Coordinate new_model = status.getModelCoordinate(mouse);

        //the calculated delta also needs to be scaled to the factor and then applied to the status
        Coordinate delta = new_model.minus(current_model).multiply(factor);
        status.padding(delta);

        Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
        this.eventBus.fireEventFromSource(new DiagramZoomEvent(factor, visibleArea), this);
        this.forceDraw = true;  //IMPORTANT: Please leave it at the very end after the event firing
    }
}