package org.reactome.web.diagram.util.svg;

import com.google.gwt.dom.client.Element;
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
import org.reactome.web.diagram.util.svg.handlers.SVGThumbnailAreaMovedHandler;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGPanel extends AbstractSVGPanel implements SVGLoader.Handler, DatabaseObjectCreatedHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        LayoutLoadedHandler, DiagramLoadRequestHandler, ControlActionHandler, CanvasExportRequestedHandler,
        SVGAnimationHandler, SVGThumbnailAreaMovedHandler {

    private static final String PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";

    private static final String CURSOR = "cursor: pointer;";
    private static final float MAX_ZOOM = 8.0f;
    private static final float MIN_ZOOM = 0.05f;

    private DiagramContext context;
    private RegExp regExp;

    private OMSVGDefsElement defs;

    private boolean isPanning;
    private boolean avoidClicking;

    private OMSVGPoint origin;

    private SVGLoader svgLoader;
    private SVGAnimation animation;

    public SVGPanel(EventBus eventBus, int width, int height) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGPanel");
        this.getElement().getStyle().setBackgroundColor("green");

        regExp = RegExp.compile(PATTERN);
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
        String svgContent = svg.getMarkup();
        if(svgContent != null) {
            Image image = new Image();
            image.setUrl("data:image/svg+xml," + svgContent);
            final ImageDownloadDialog downloadDialogBox = new ImageDownloadDialog(image, "svg", "stableID");
            downloadDialogBox.show();
        }
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {
        setVisible(false);
        context = null;
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
        origin = getTranslatedPoint(event);
        isPanning = true;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.preventDefault(); event.stopPropagation();
        if(isPanning) {
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
        OMElement el = (OMElement) event.getSource();
        if(!avoidClicking && (el.getId().matches(PATTERN))){
            Console.info(el.getTagName() + ": " + el.getAttribute("id") + " was clicked!");
            DatabaseObjectFactory.get(el.getAttribute("id"), this);
        }
        isPanning = false;
        avoidClicking = false;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl("shadowFilter"));
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, "");
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

        OMSVGGElement gElement = new OMSVGGElement();
        OMNodeList<OMElement> children = svg.getElementsByTagName(gElement.getTagName());
        for (OMElement child : children) {
            if(regExp.test(child.getId())) {
                child.addDomHandler(SVGPanel.this, MouseUpEvent.getType());
                child.addDomHandler(SVGPanel.this, MouseOverEvent.getType());
                child.addDomHandler(SVGPanel.this, MouseOutEvent.getType());
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
        // Add primitives
        OMSVGFEOffsetElement offSet = new OMSVGFEOffsetElement();
        offSet.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        offSet.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "offOut");

        OMSVGFEGaussianBlurElement blur = new OMSVGFEGaussianBlurElement();
        blur.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, "offOut");
        blur.setAttribute(SVGConstants.SVG_STD_DEVIATION_ATTRIBUTE, "5");
        blur.setAttribute(SVGConstants.SVG_RESULT_ATTRIBUTE, "blurOut");

        OMSVGFEBlendElement blend = new OMSVGFEBlendElement();
        blend.setAttribute(SVGConstants.SVG_IN_ATTRIBUTE, OMSVGFilterElement.IN_SOURCE_GRAPHIC);
        blend.setAttribute(SVGConstants.SVG_IN2_ATTRIBUTE, "blurOut");
        blend.setAttribute(SVGConstants.SVG_MODE_ATTRIBUTE, "normal");

        //Compose the filter from the primitives
        OMSVGFilterElement shadowFilter = new OMSVGFilterElement();
        shadowFilter.setId("shadowFilter");
        shadowFilter.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, "-25%");
        shadowFilter.setAttribute(SVGConstants.SVG_WIDTH_ATTRIBUTE, "150%");
        shadowFilter.setAttribute(SVGConstants.SVG_HEIGHT_ATTRIBUTE, "150%");
//        shadowFilter.setAttribute(SVGConstants.SVG_FILTER_RES_ATTRIBUTE, "200");

        shadowFilter.appendChild(offSet);
        shadowFilter.appendChild(blur);
        shadowFilter.appendChild(blend);

        defs = new OMSVGDefsElement();
        defs.appendChild(shadowFilter);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        eventBus.addHandler(ControlActionEvent.TYPE, this);
        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);
        eventBus.addHandler(SVGThumbnailAreaMovedEvent.TYPE, this);
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
