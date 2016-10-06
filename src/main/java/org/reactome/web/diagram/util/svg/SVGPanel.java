package org.reactome.web.diagram.util.svg;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.analysis.client.model.PathwaySummary;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.loader.SVGLoader;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.svg.animation.SVGAnimation;
import org.reactome.web.diagram.util.svg.animation.SVGAnimationHandler;
import org.reactome.web.diagram.util.svg.events.SVGLoadedEvent;
import org.reactome.web.diagram.util.svg.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.util.svg.handlers.SVGLoadedHandler;
import org.reactome.web.diagram.util.svg.handlers.SVGThumbnailAreaMovedHandler;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGPanel extends AbstractSVGPanel implements DatabaseObjectCreatedHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        DiagramLoadRequestHandler, DiagramLoadedHandler, ControlActionHandler, CanvasExportRequestedHandler,
        SVGLoadedHandler, SVGAnimationHandler, SVGThumbnailAreaMovedHandler, DoubleClickHandler,
        AnalysisResultLoadedHandler, AnalysisProfileChangedHandler, AnalysisResetHandler, ExpressionColumnChangedHandler {

    private static final String STID_PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";

    private static final String CLIPPING_PATH = "CLIPPING_PATH_";
    private static final String CLIPPING_RECT = "CLIPPING_RECT_";
    private static final float MIN_OVERLAY = 0.05f;

    private static final String CURSOR = "cursor: pointer;";
    private static final float MAX_ZOOM = 8.0f;
    private static final float MIN_ZOOM = 0.05f;

    private DiagramContext context;
    private RegExp regExp;

    private OMSVGDefsElement defs;

    private boolean isPanning;
    private boolean avoidClicking;

    private OMElement selected;
    private OMSVGPoint origin;

    private SVGLoader svgLoader;
    private SVGAnimation animation;

    private AnalysisType analysisType;
    private ExpressionSummary expressionSummary;
    private List<PathwaySummary> pathwaySummaries;
    private int selectedExpCol = 0;

    public SVGPanel(EventBus eventBus, int width, int height) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGPanel");

        regExp = RegExp.compile(STID_PATTERN);

        initHandlers();
        setSize(width, height);
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        clearOverlay();
        overlayAnalysisResults();
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        analysisType = AnalysisType.NONE;
        expressionSummary = null;
        pathwaySummaries = null;
        selectedExpCol = 0;

        clearOverlay();
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        //!!! Important !!!
        //For the moment analysis results are loaded from the EVENT, as the context does not yet include them.
        //TODO: When integration of the SVGPanel is completed, Analysis results have to be read by the context

        analysisType = event.getType();
        pathwaySummaries = event.getPathwaySummaries();
        expressionSummary = event.getExpressionSummary();

        clearOverlay();
        overlayAnalysisResults();
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        switch (event.getAction()) {
            case FIT_ALL:       fitALL(true);                     break;
            case ZOOM_IN:       zoom(1.1f, getCentrePoint());     break;
            case ZOOM_OUT:      zoom(0.9f, getCentrePoint());     break;
            case UP:            translate(0, 10);                 break;
            case RIGHT:         translate(-10, 0);                break;
            case DOWN:          translate(0, -10);                break;
            case LEFT:          translate(10, 0);                 break;
        }
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        if(databaseObject instanceof Pathway) {
            Pathway p = (Pathway) databaseObject;
            eventBus.fireEventFromSource(new DiagramLoadRequestEvent(p), this);
        }
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {
        Console.error("Error getting pathway information...");
        //TODO: Decide what to do in this case
    }

    @Override
    public void onDiagramExportRequested(CanvasExportRequestedEvent event) {
//        exportSVG(context.getContent().getStableId());
        exportSVG(svg.getLocalName());
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        setVisible(false);
        context = null;
        svg = null;
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        DatabaseObjectFactory.get(el.getAttribute("id"), this);
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        selectedExpCol = e.getColumn();
        clearOverlay();
        overlayAnalysisResults();
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault(); event.stopPropagation();
        if(animation!=null) animation.cancel();

        int button = event.getNativeEvent().getButton();
        switch (button) {
            case NativeEvent.BUTTON_RIGHT:
                //TODO implement the context menu
                break;
            default:
                origin = getTranslatedPoint(event);
                isPanning = true;
                break;
        }
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
            if ((el.getId().matches(STID_PATTERN))) {
                setSelected(el);
            } else {
                resetSelected();
            }
        }
        isPanning = false;
        avoidClicking = false;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        if(!el.equals(selected)) {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_FILTER));
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(COMBINED_FILTER));
        }
        applyCTM(false);  //TODO Have a look why this is required
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        if(!el.equals(selected)) {
            el.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
        }
        applyCTM(false);  //TODO Have a look why this is required
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault(); event.stopPropagation();
        float delta = event.getDeltaY() * 0.020f;
        float zoom = (1 - delta) > 0 ? (1 - delta) : 1;
        zoom(zoom, getTranslatedPoint(event));
    }

    @Override
    public void onSVGLoaded(SVGLoadedEvent event) {
        setVisible(true);
        this.svg = event.getSVG();

        entities = new ArrayList();
        OMNodeList<OMElement> children = svg.getElementsByTagName(new OMSVGGElement().getTagName());
        for (OMElement child : children) {
            if(regExp.test(child.getId())) {
                entities.add(child);
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

        // !!! Important !!! //
        // Adding the MouseWheelEvent directly on the SVG is not working
        // on certain browsers. This is why we are adding the event handling
        // on the wrapping div.
        this.addDomHandler(this, MouseWheelEvent.getType());

        // Remove viewbox and set size
        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        setSize(getOffsetWidth(), getOffsetHeight());

        Element div = SVGPanel.this.getElement();
        if(div.hasChildNodes()) {
            div.replaceChild(svg.getElement(), div.getFirstChild());
        } else {
            div.appendChild(svg.getElement());
        }

        // Set initial translation matrix
        initialTM = getInitialCTM();
        initialBB = svg.getBBox();
        ctm = initialTM;
        fitALL(false);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        setVisible(false); //fall back to the green boxes diagram
        svg = null;
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

    private void clearOverlay() {
        for (OMElement entity : entities) {
            String stId = entity.getId();
            OMElement overlay = svg.getElementById(stId +"_Overlay");
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
        OMSVGGElement overlay = (OMSVGGElement) svg.getElementById(stId +"_Overlay");
        if(overlay == null) {
            overlay = (OMSVGGElement) svg.getElementById(stId).cloneNode(true);
            overlay.setId(stId + "_Overlay");
            overlay.setAttribute(SVGConstants.SVG_CLIP_PATH_ATTRIBUTE, DOMHelper.toUrl(CLIPPING_PATH + stId));
            overlay.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            overlay.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
            overlay.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, overlayColour);

            OMSVGGElement overlayGroup = new OMSVGGElement();
            overlayGroup.appendChild(overlay);

            //Handling text elements
            OMNodeList<OMElement> textElements = getAllTextElementsFrom(overlay);
            Iterator<OMElement> it = textElements.iterator();
            while(it.hasNext()){
                overlayGroup.appendChild(it.next());
            }
            removeAttributeFromChildren(overlay, SVGConstants.SVG_CLASS_ATTRIBUTE);
            svg.getElementById(stId).appendChild(overlayGroup);
        }
    }

    private void fitALL(boolean animated) {
        OMSVGMatrix fitTM = calculateFitAll(FRAME);
        if(animated) {
            animation = new SVGAnimation(this, ctm);
            animation.animate(fitTM);
        } else {
            ctm = initialTM.multiply(fitTM);
            applyCTM(true);
        }
    }

    private double getRatio(PathwaySummary p) {
        EntityStatistics stats = p.getEntities();
        Integer found = stats.getFound() != null ? stats.getFound() : 0;
        Integer total = stats.getTotal() != null ? stats.getTotal() : 0;
        Double rtn = found / (double) total;
        return !rtn.isInfinite() && !rtn.isNaN() ? rtn : 0.0;
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        eventBus.addHandler(ControlActionEvent.TYPE, this);
        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(SVGLoadedEvent.TYPE, this);
        eventBus.addHandler(SVGThumbnailAreaMovedEvent.TYPE, this);

        eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
    }

    private void overlayAnalysisResults() {
        for (PathwaySummary p : pathwaySummaries) {
            overlayEntity(p);
        }
    }

    private void overlayEntity(PathwaySummary pathwaySummary) {
        OMElement el = svg.getElementById(pathwaySummary.getStId());
        if(el!=null) {
            switch (analysisType) {
                case SPECIES_COMPARISON:
                case OVERREPRESENTATION:
                    String enrichColour = hex2Rgb(AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax(), 0.9f);
                    overlayEntity(pathwaySummary.getStId(), (float) getRatio(pathwaySummary), enrichColour);
                    break;
                case EXPRESSION:
                    String expressionColour = AnalysisColours.get().expressionGradient.getColor(
                            pathwaySummary.getEntities().getExp().get(selectedExpCol),
                            expressionSummary.getMin(),
                            expressionSummary.getMax()
                    );
                    overlayEntity(pathwaySummary.getStId(), (float) getRatio(pathwaySummary), hex2Rgb(expressionColour, 0.9f) );
                    break;
            }
        }
    }

    private void overlayEntity(String stId, float percentage, String overlayColour) {
        createOrUpdateClippingPath(stId, percentage);
        createOrUpdateOverlayElement(stId, overlayColour);
    }

    private void resetSelected() {
        if(selected!=null) {
            selected.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            selected = null;
        }
    }

    private void setSelected(OMElement element) {
        if(selected!=null && !selected.equals(element)) {
            resetSelected();
        }
        element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(COMBINED_FILTER));
        selected = element;
        applyCTM(false);  //TODO Have a look why this is required
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

}
