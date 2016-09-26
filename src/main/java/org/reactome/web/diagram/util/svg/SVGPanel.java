package org.reactome.web.diagram.util.svg;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.context.popups.ImageDownloadDialog;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.CanvasExportRequestedHandler;
import org.reactome.web.diagram.handlers.ControlActionHandler;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.svg.animation.SVGAnimation;
import org.reactome.web.diagram.util.svg.animation.SVGAnimationHandler;
import org.reactome.web.diagram.util.svg.events.SVGLoadedEvent;
import org.reactome.web.diagram.util.svg.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.util.svg.filters.FilterColour;
import org.reactome.web.diagram.util.svg.filters.FilterFactory;
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


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGPanel extends AbstractSVGPanel implements SVGLoader.Handler, DatabaseObjectCreatedHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        LayoutLoadedHandler, DiagramLoadRequestHandler, ControlActionHandler, CanvasExportRequestedHandler,
        SVGAnimationHandler, SVGThumbnailAreaMovedHandler, DoubleClickHandler {

    private static final String STID_PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";
    private static final String HOVERRING_FILTER = "shadowFilter";
    private static final String SELECTION_FILTER = "selectionFilter";
    private static final String COMBINED_FILTER = "combinedFilter";

    private static final String CLIPPING_PATH = "CLIPPING_PATH_";
    private static final String CLIPPING_RECT = "CLIPPING_RECT_";

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

    public SVGPanel(EventBus eventBus, int width, int height) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGPanel");
        this.getElement().getStyle().setBackgroundColor("green");

        regExp = RegExp.compile(STID_PATTERN);
        svgLoader = new SVGLoader(this);

        initFilters();
        initHandlers();
        setSize(width, height);
    }

    public void load(String cPicture) {
        setVisible(false);
        svgLoader.load(cPicture);
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
            case FIREWORKS:
                testClipping("R-HSA-169911.1", 0.3f);
                testClipping("R-HSA-5357769.1", 0.3f);
                testClipping("R-HSA-75153", 0.6f);
                testClipping("R-HSA-109606.1", 0.7f);

                break;
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
        if(svg != null) {
            Image image = new Image();
            image.setUrl("data:image/svg+xml," + svg.getMarkup());
            final ImageDownloadDialog downloadDialogBox = new ImageDownloadDialog(image, "svg", context.getContent().getStableId());
            downloadDialogBox.show();
        }
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
        Console.error(el.getId() + " was double clicked!");
        DatabaseObjectFactory.get(el.getAttribute("id"), this);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        context = event.getContext();
        DiagramContent content = context.getContent();
        if(content.getCPicture() != null && content.getCPicture()) {
            load(content.getStableId());
        }
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
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERRING_FILTER));
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(COMBINED_FILTER));
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        if(!el.equals(selected)) {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, "");
        } else {
            el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
        }
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault(); event.stopPropagation();
        float delta = event.getDeltaY() * 0.020f;
        float zoom = (1 - delta) > 0 ? (1 - delta) : 1;
        zoom(zoom, getTranslatedPoint(event));
    }

    @Override
    public void onSvgLoaded(OMSVGSVGElement svg, long time) {
        setVisible(true);
        this.svg = svg;

        //TODO: to be removed and added in the LoaderManager
        eventBus.fireEventFromSource(new SVGLoadedEvent(svg, time), this);

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

        // Attach filters to the root SVG structure
        svg.appendChild(defs);

        // Add the event handlers
        svg.addMouseDownHandler(this);
        svg.addMouseMoveHandler(this);
        svg.addMouseUpHandler(this);
        svg.addDomHandler(SVGPanel.this, MouseWheelEvent.getType());

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
        initialTM = svg.getCTM();
        initialBB = svg.getBBox();
        ctm = initialTM;
        fitALL(false);
    }

    @Override
    public void onSvgLoaderError(Throwable exception) {
        Console.error("Error loading SVG...");
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

    private void testClipping(String stId, float percentage) {
        createOrUpdateClippingPath(stId, percentage);
        createOrUpdateOverlayElement(stId, "rgba(255,255,0,0.9)");
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


    private void createOrUpdateClippingPath(String stId, float percentage){
        OMSVGRectElement rect = (OMSVGRectElement) svg.getElementById(CLIPPING_RECT + stId);

        if(rect == null) {
            //Clipping path is not present.
            rect = new OMSVGRectElement(0.0f, 0.0f, percentage, 1f, 0, 0);
            rect.setId(CLIPPING_RECT + stId);

            OMSVGClipPathElement cp = new OMSVGClipPathElement();
            cp.setId(CLIPPING_PATH + stId);
            cp.setAttribute(SVGConstants.SVG_CLIP_PATH_UNITS_ATTRIBUTE, SVGConstants.SVG_OBJECT_BOUNDING_BOX_VALUE);
            cp.appendChild(rect);
            defs.appendChild(cp);
        } else {
            //Clipping path exists, simply re-use it
            rect.getWidth().getBaseVal().setValue(percentage);
        }
    }

    private void createOrUpdateOverlayElement(String stId, String overlayColor) {
        OMSVGGElement overlay = (OMSVGGElement) svg.getElementById(stId +"_Overlay");
        if(overlay == null) {
            overlay = (OMSVGGElement) svg.getElementById(stId).cloneNode(true);
            overlay.setId(stId + "_Overlay");
            overlay.setAttribute(SVGConstants.SVG_CLIP_PATH_ATTRIBUTE, DOMHelper.toUrl(CLIPPING_PATH + stId));
            overlay.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
            overlay.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, overlayColor);

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

    private void initFilters() {
        defs = new OMSVGDefsElement();
        defs.appendChild(FilterFactory.getShadowFilter(HOVERRING_FILTER));
        defs.appendChild(FilterFactory.getOutlineFilter(SELECTION_FILTER, FilterColour.BLUE));
        defs.appendChild(FilterFactory.combine(COMBINED_FILTER, FilterFactory.getShadowFilter(HOVERRING_FILTER), FilterFactory.getOutlineFilter(SELECTION_FILTER, FilterColour.BLUE)));
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        eventBus.addHandler(ControlActionEvent.TYPE, this);
        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);
        eventBus.addHandler(SVGThumbnailAreaMovedEvent.TYPE, this);
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
        //TODO Have a look why this is required
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
}
