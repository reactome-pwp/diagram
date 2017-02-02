package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.analysis.client.model.PathwaySummary;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.ehld.animation.SVGAnimation;
import org.reactome.web.diagram.client.visualisers.ehld.animation.SVGAnimationHandler;
import org.reactome.web.diagram.client.visualisers.ehld.context.SVGContextPanel;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.client.visualisers.ehld.handlers.SVGThumbnailAreaMovedHandler;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.content.EHLDContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.thumbnail.ehld.SVGThumbnail;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.svg.SVGUtil;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.reactome.web.diagram.events.CanvasExportRequestedEvent.Option;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGPanel extends AbstractSVGPanel implements Visualiser,
        AnalysisProfileChangedHandler, DatabaseObjectCreatedHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        DoubleClickHandler, ContextMenuHandler,
        SVGAnimationHandler, SVGThumbnailAreaMovedHandler {

    private static final String REGION = "REGION-";
    private static final String OVERLAY ="OVERLAY-";
    private static final String OVERLAY_CLONE ="OVERLAYCLONE-";

    private static final String CLIPPING_PATH = "CLIPPINGPATH-";
    private static final String CLIPPING_RECT = "CLIPPINGRECT-";
    private static final float MIN_OVERLAY = 0.05f;

    private static final String CURSOR = "cursor: pointer;";
    private static final float ZOOM_IN_STEP = 1.1f;
    private static final float ZOOM_OUT_STEP = 0.9f;
    private static final float MAX_ZOOM = 8.0f;
    private static final float MIN_ZOOM = 0.05f;

    private Context context;

    private OMSVGDefsElement defs;

    private boolean initialised;
    private int viewportWidth = 0;
    private int viewportHeight = 0;

    private boolean isPanning;
    private boolean avoidClicking;

    private OMElement selected;
    private OMElement hovered;
    private OMSVGPoint origin;

    private SVGAnimation animation;

    private AnalysisType analysisType;
    private ExpressionSummary expressionSummary;
    private int selectedExpCol = 0;

    private SVGContextPanel contextPanel;
    private Thumbnail thumbnail;

    public SVGPanel(EventBus eventBus) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGPanel");
        initHandlers();
//        setSize(width, height);
    }

    protected void initialise() {
        if (!initialised) {
            this.initialised = true;

            contextPanel = new SVGContextPanel(eventBus);
            thumbnail = new SVGThumbnail(eventBus);
            this.add(thumbnail);

            this.viewportWidth = getParent().getOffsetWidth();
            this.viewportHeight = getParent().getOffsetHeight();
//            setSize(viewportWidth, viewportHeight);
        }
    }

    @Override
    public void fitDiagram(boolean animation) {
        fitALL(true);
    }

    @Override
    public void zoomDelta(double deltaFactor) {
        zoom((float)deltaFactor, getCentrePoint());
    }

    @Override
    public void zoomIn() {
        zoom(ZOOM_IN_STEP, getCentrePoint());
    }

    @Override
    public void zoomOut() {
        zoom(ZOOM_OUT_STEP, getCentrePoint());
    }

    @Override
    public void padding(int dX, int dY) {
        translate(dX, dY);
    }

    @Override
    public void exportView(Option option) {
        if (context != null) {
            exportView(context.getContent().getStableId());
        }
    }

    @Override
    public void contentLoaded(Context context) {
        setContext(context);
    }

    @Override
    public void contentRequested() {
        context = null;
        if(svg!=null) {
            if(getElement().getChildCount()>1) {
                svg.getElement().removeFromParent();
//                getElement().getLastChild().removeFromParent();
            }
            svg = null;
        }
        thumbnail.contentRequested();
    }


    @Override
    public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
        boolean rtn = false;
        if (graphObject != null) {
            SVGEntity svgEntity = entities.get(graphObject.getStId());
            if (svgEntity != null && hovered != svgEntity.getHoverableElement()) {
                resetHighlight(false);
                highlightElement(svgEntity.getHoverableElement());
                thumbnail.setHoveredItem(svgEntity.getHoverableElement().getId());
                if (notify) {
                    eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject), this);
                }
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public boolean resetHighlight(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (hovered != null) {
            unHighlightElement(hovered);
            thumbnail.setHoveredItem(null);
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectHoveredEvent(), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
        boolean rtn = false;
        if (graphObject != null) {
            SVGEntity svgEntity = entities.get(graphObject.getStId());
            if (svgEntity != null && selected != svgEntity.getHoverableElement()) {
                resetSelection(false);
                setSelectedElement(svgEntity.getHoverableElement());
                thumbnail.setSelectedItem(svgEntity.getHoverableElement().getId());
                if (notify) {
                    eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject), this);
                }
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public boolean resetSelection(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (selected != null) {
            resetSelectedElement();
            thumbnail.setSelectedItem(null);
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        if(context!=null && svg!=null) {
            clearOverlay();
            overlayAnalysisResults();
        }
    }

    @Override
    public void resetAnalysis() {
        analysisType = AnalysisType.NONE;
        expressionSummary = null;
        selectedExpCol = 0;
        if(svg!=null) {
            clearOverlay();
        }
    }

//    @Override
//    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
//        //!!! Important !!!
//        //For the moment analysis results are loaded from the EVENT, as the context does not yet include them.
//        //TODO: When integration of the SVGPanel is completed, Analysis results have to be read by the context
//        analysisType = event.getType();
//        pathwaySummaries = event.getPathwaySummaries();
//        expressionSummary = event.getExpressionSummary();
//        selectedExpCol = 0;
//        if(svg!=null) {
//            clearOverlay();
//            overlayAnalysisResults();
//        }
//    }

    @Override
    public void loadAnalysis() {
        AnalysisStatus analysisStatus = context.getAnalysisStatus();
        analysisType = analysisStatus.getAnalysisType();
        expressionSummary = analysisStatus.getExpressionSummary();
        selectedExpCol = 0;
        if(svg!=null) {
            clearOverlay();
            overlayAnalysisResults();
        }
    }

    @Override
    public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        if(databaseObject instanceof Pathway) {
            Pathway p = (Pathway) databaseObject;
            eventBus.fireEventFromSource(new ContentRequestedEvent(p.getDbId() + ""), this);
        }
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {
        Console.error("Error getting pathway information...");
        //TODO: Decide what to do in this case
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        String stableId = SVGUtil.keepStableId(el.getAttribute("id"));
        if(stableId != null) {
            DatabaseObjectFactory.get(stableId, this);
        }
    }

//    @Override
//    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
//        selectedExpCol = e.getColumn();
//        if(svg!=null) {
//            clearOverlay();
//            overlayAnalysisResults();
//        }
//    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(() -> initialise());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault(); event.stopPropagation();
        if(animation!=null) animation.cancel();
        origin = getTranslatedPoint(event);
        isPanning = true;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.preventDefault(); event.stopPropagation();
        if(isPanning) {
            getElement().getStyle().setCursor(Style.Cursor.MOVE);
            avoidClicking = true;
            OMSVGPoint end = getTranslatedPoint(event);

            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(end.getX() - origin.getX(), end.getY() - origin.getY());
            ctm = ctm.multiply(newMatrix);

            applyCTM(true);
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.preventDefault(); event.stopPropagation();
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        OMElement el = (OMElement) event.getSource();
        if(!avoidClicking) {
            String elementId = el.getId();
            if(SVGUtil.isAnnotated(elementId)) {
                int button = event.getNativeEvent().getButton();
                switch (button) {
                    case NativeEvent.BUTTON_RIGHT:
                        contextPanel.show(SVGUtil.keepStableId(elementId), event.getClientX(), event.getClientY());
                        break;
                    default:
                        if (selected != el) {
                            setSelectedElement(el);
                            thumbnail.setSelectedItem(elementId);
                            notifySelection(elementId);
                        }
                        break;
                }
            } else {
                if(selected != null) {
                    resetSelectedElement();
                    thumbnail.setSelectedItem(null);
                    notifySelection(null);
                }
            }
        }
        isPanning = false;
        avoidClicking = false;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        resetHighlight(false);
        highlightElement(el);
        applyCTM(false);  //TODO Have a look why this is required
        thumbnail.setHoveredItem(el.getId());
        notifyHovering(el.getId());
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        unHighlightElement(el);
        applyCTM(false);  //TODO Have a look why this is required
        thumbnail.setHoveredItem(null);
        notifyHovering(null);
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault(); event.stopPropagation();
        contextPanel.hide();
        float delta = event.getDeltaY() * 0.020f;
        float zoom = (1 - delta) > 0 ? (1 - delta) : 1;
        zoom(zoom, getTranslatedPoint(event));
    }

    @Override
    public void onSVGThumbnailAreaMoved(SVGThumbnailAreaMovedEvent event) {
        OMSVGPoint padding = event.getPadding();
        ctm = ctm.translate(padding.getX(), padding.getY());
        applyCTM(true);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        notifyAboutChangeInView();
    }

    public void transform(OMSVGMatrix newTM){
        ctm = newTM;
        applyCTM(true);
    }

    private void applyCTM(boolean fireEvent) {
        sb.setLength(0);
        sb.append("matrix(").append(ctm.getA()).append(",").append(ctm.getB()).append(",").append(ctm.getC()).append(",")
                .append(ctm.getD()).append(",").append(ctm.getE()).append(",").append(ctm.getF()).append(")");
        for (OMSVGElement svgLayer : svgLayers) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }
        zFactor = ctm.getA();

        if(fireEvent) {
            notifyAboutChangeInView();
        }
    }

    private SVGEntity addOrUpdateSVGEntity(OMElement element) {
        String elementId = element.getId();
        String stId = elementId.substring(elementId.indexOf("-") + 1);
        SVGEntity entity = entities.get(stId);
        if(entity == null) {
            entity = new SVGEntity(stId);
            entities.put(stId, entity);
        }

        if(elementId.startsWith(REGION)) {
            entity.setRegion(element);
        } else if(elementId.startsWith(OVERLAY)) {
            entity.setOverlay(element);
        }
        return entity;
    }

    private void clearOverlay() {
        for (SVGEntity entity : entities.values()) {
            String stId = entity.getStId();
            OMElement overlay = svg.getElementById(OVERLAY_CLONE + stId);
            if(overlay != null) {
                OMNode parent = overlay.getParentNode();
                parent.getParentNode().removeChild(parent);
            }
        }
    }


    private void createOrUpdateClippingPath(String stId, float ratio){
        OMSVGRectElement rect = (OMSVGRectElement) svg.getElementById(CLIPPING_RECT + stId);
        // Important !!! Correct ratio so that it is visible
        ratio = ratio > MIN_OVERLAY ? ratio : MIN_OVERLAY;

        if(rect == null) {
            //Clipping path is not present.
            rect = new OMSVGRectElement(0.0f, 0.0f, ratio, 1f, 0, 0);
            rect.setId(CLIPPING_RECT + stId);

            OMSVGClipPathElement cp = new OMSVGClipPathElement();
            cp.setId(CLIPPING_PATH + stId);
            cp.setAttribute(SVGConstants.SVG_CLIP_PATH_UNITS_ATTRIBUTE, SVGConstants.SVG_OBJECT_BOUNDING_BOX_VALUE);
            cp.appendChild(rect);
            defs.appendChild(cp);
        } else {
            //Clipping path exists, simply re-use it
            rect.getWidth().getBaseVal().setValue(ratio);
        }
    }

    private void createOrUpdateOverlayElement(String stId, String overlayColour) {
        OMSVGGElement overlay = (OMSVGGElement) svg.getElementById(OVERLAY_CLONE + stId);
        if(overlay == null) {
            SVGEntity entity = entities.get(stId); //Entity cannot be null as it has been checked in a previous step.
            overlay = (OMSVGGElement) entity.getOverlay().cloneNode(true);
            overlay.setId(OVERLAY_CLONE + stId);
            overlay.setAttribute(SVGConstants.SVG_CLIP_PATH_ATTRIBUTE, DOMHelper.toUrl(CLIPPING_PATH + stId));
            overlay.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            overlay.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
            overlay.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, overlayColour);

            // Create a group to put the clone and the text
            OMSVGGElement overlayGroup = new OMSVGGElement();
            overlayGroup.appendChild(overlay);

            // Make sure all text elements are put in front of the overlay
            List<OMElement> textElements = getAllTextElementsFrom(overlay);
            Iterator<OMElement> it = textElements.iterator();
            while(it.hasNext()){
                overlayGroup.appendChild(it.next());
            }
            // Remove styling and add the overlay group under the OVERLAY-R-SSS-NNNNNNN
            removeAttributeFromChildren(overlay, SVGConstants.SVG_CLASS_ATTRIBUTE);
            entity.getOverlay().appendChild(overlayGroup);
        }
    }

    private void fitALL(boolean animated) {
        OMSVGMatrix fitTM = calculateFitAll(FRAME);

        if(!SVGUtil.areEqual(ctm, fitTM)) {
            if (animated) {
                animation = new SVGAnimation(this, ctm);
                animation.animate(fitTM);
            } else {
                ctm = initialTM.multiply(fitTM);
                applyCTM(true);
            }
        }
    }

    @Deprecated
    private double getRatio(PathwaySummary p) {
        EntityStatistics stats = p.getEntities();
        Integer found = stats.getFound() != null ? stats.getFound() : 0;
        Integer total = stats.getTotal() != null ? stats.getTotal() : 0;
        Double rtn = found / (double) total;
        return !rtn.isInfinite() && !rtn.isNaN() ? rtn : 0.0;
    }

    private void highlightElement(OMElement el){
        hovered = el;
        if(!el.equals(selected)) {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_FILTER));
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_HOVERING_FILTER));
        }
    }

    private void unHighlightElement(OMElement el){
        hovered = null;
        if(!el.equals(selected)) {
            el.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
        }
    }

    private void initHandlers() {
        eventBus.addHandler(SVGThumbnailAreaMovedEvent.TYPE, this);
        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);

//        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
//        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);

        // !!! Important !!! //
        // Adding the MouseWheelEvent directly on the SVG is not working
        // on certain browsers. This is why we are adding the event handling
        // on the wrapping div.
        addDomHandler(this, MouseWheelEvent.getType());
        addDomHandler(SVGPanel.this, ContextMenuEvent.getType());
    }

    private void notifyAboutChangeInView() {
        if(svg != null && ctm !=null) {
            OMSVGPoint from = svg.createSVGPoint(0, 0);
            from = from.matrixTransform(ctm.inverse());

            OMSVGPoint to = svg.createSVGPoint(getOffsetWidth(), getOffsetHeight());
            to = to.matrixTransform(ctm.inverse());

            thumbnail.diagramZoomEvent(new Box(from.getX(), from.getY(), to.getX(), to.getY()));
        }
    }

    private void notifySelection(String elementId){
        GraphObject selected = null;
        if(elementId!=null) {
            selected = context.getContent().getDatabaseObject(SVGUtil.keepStableId(elementId));
        }
        eventBus.fireEventFromSource(new GraphObjectSelectedEvent(selected, false), this);
    }

    private void notifyHovering(String elementId){
        GraphObject hovered = null;
        if(elementId!=null) {
            hovered = context.getContent().getDatabaseObject(SVGUtil.keepStableId(elementId));
        }
        eventBus.fireEventFromSource(new GraphObjectHoveredEvent(hovered), this);
    }

    private void overlayAnalysisResults() {
        for (GraphPathway graphPathway : context.getContent().getEncapsulatedPathways()){
            overlayEntity(graphPathway);
        }
    }

    private void overlayEntity(GraphPathway graphPathway) {
        SVGEntity entity = entities.get(graphPathway.getStId());
        if(entity!=null) {
            OMElement el = entity.getOverlay();
            if(el!=null) {
                float percentage;
                switch (analysisType) {
                    case SPECIES_COMPARISON:
                    case OVERREPRESENTATION:
                        String enrichColour = hex2Rgb(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax(), 0.9f);
                        percentage = graphPathway.isHit() ? graphPathway.getPercentage().floatValue() : MIN_OVERLAY;
                        overlayEntity(graphPathway.getStId(), percentage, enrichColour);
                        break;
                    case EXPRESSION:
                        percentage = graphPathway.isHit() ? graphPathway.getPercentage().floatValue() : MIN_OVERLAY;
                        String expressionColour = AnalysisColours.get().expressionGradient.getColor(
                                graphPathway.getExpression(selectedExpCol).floatValue(),
                                expressionSummary.getMin(),
                                expressionSummary.getMax()
                        );
                        overlayEntity(graphPathway.getStId(), percentage, hex2Rgb(expressionColour, 0.9f));
                        break;
                }
            }
        }
    }

    private void overlayEntity(String stId, float percentage, String overlayColour) {
        createOrUpdateClippingPath(stId, percentage);
        createOrUpdateOverlayElement(stId, overlayColour);
    }

    private void resetSelectedElement() {
        if(selected!=null) {
            selected.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            selected = null;
        }
    }

    private void setSelectedElement(OMElement element) {
        if(selected!=null && !selected.equals(element)) {
            resetSelectedElement();
        }
        element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_HOVERING_FILTER));
        selected = element;
        applyCTM(false);
    }

    private void translate(float x, float y) {
        OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(x, y);
        ctm = ctm.multiply(newMatrix);
        applyCTM(true);
    }

    private void zoom(float zoom, OMSVGPoint c) {
        if(zoom != 1 && (zFactor * zoom <= MAX_ZOOM) && (zFactor * zoom >= MIN_ZOOM)) {
            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(c.getX(), c.getY()).scale(zoom).translate(-c.getX(), -c.getY());
            ctm = ctm.multiply(newMatrix);
            applyCTM(true);
        }
    }

    @Override
    public void highlightInteractor(DiagramInteractor diagramInteractor) {
        //Nothing here
    }

    @Override
    public GraphObject getSelected() {
        GraphObject rtn = null;
        if(context!=null && selected !=null) {
            rtn = context.getContent().getDatabaseObject(SVGUtil.keepStableId(selected.getId()));
        }
        return rtn;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
        Content content = context.getContent();
        this.svg = (OMSVGSVGElement) ((EHLDContent)content).getSVG().cloneNode(true);

        entities = new HashMap<>();
        for (OMElement child : SVGUtil.getAnnotatedOMElements(svg)) {
            addOrUpdateSVGEntity(child);
        }

        for (SVGEntity svgEntity : entities.values()) {
            OMElement child = svgEntity.getHoverableElement();
            if(child!=null) {
                child.addDomHandler(SVGPanel.this, MouseUpEvent.getType());
                child.addDomHandler(SVGPanel.this, MouseOverEvent.getType());
                child.addDomHandler(SVGPanel.this, MouseOutEvent.getType());
                child.addDomHandler(SVGPanel.this, DoubleClickEvent.getType());
                // Set the pointer to the active regions
                child.setAttribute("style", CURSOR);
            }
        }

        // Identify all layers by getting all top-level g elements
        svgLayers = getRootLayers();

        // Clone and attach defs (filters - clipping paths) to the root SVG structure
        defs = (OMSVGDefsElement) baseDefs.cloneNode(true);
        svg.appendChild(defs);

        // Add the event handlers
        svg.addMouseDownHandler(this);
        svg.addMouseMoveHandler(this);
        svg.addMouseUpHandler(this);

        // Remove viewbox and set size
        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        setSize(getOffsetWidth(), getOffsetHeight());

        Element div = SVGPanel.this.getElement();
        if(div.getChildCount()>1) {
            div.replaceChild(svg.getElement(), div.getLastChild());
        } else {
            div.appendChild(svg.getElement());
        }

        // Render thumbnail
        thumbnail.diagramRendered(content, null);

        // Set initial translation matrix
        initialTM = getInitialCTM();
        initialBB = svg.getBBox();
        ctm = initialTM;
        fitALL(false);
    }

    @Override
    public void resetContext() {
        this.context = null;
    }

    @Override
    public void expressionColumnChanged() {
        if (context != null) {
            selectedExpCol = context.getAnalysisStatus().getColumn();
            if (svg != null) {
                clearOverlay();
                overlayAnalysisResults();
            }
        }
    }

    @Override
    public void interactorsCollapsed(String resource) {
        //Nothing here
    }

    @Override
    public void interactorsFiltered() {
        //Nothing here
    }

    @Override
    public void interactorsLayoutUpdated() {
        //Nothing here
    }

    @Override
    public void interactorsLoaded() {
        //Nothing here
    }

    @Override
    public void interactorsResourceChanged(OverlayResource resource) {
        //Nothing here
    }

    @Override
    public void flagItems(Set<DiagramObject> flaggedItems){
        //TODO Implement this
    }

    @Override
    public void resetFlag(){
        //TODO Implement this
    }
}
