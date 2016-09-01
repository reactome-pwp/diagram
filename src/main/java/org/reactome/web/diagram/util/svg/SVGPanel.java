package org.reactome.web.diagram.util.svg;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.context.popups.ImageDownloadDialog;
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
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGPanel extends AbsolutePanel implements SVGLoader.Handler, DatabaseObjectCreatedHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        LayoutLoadedHandler, DiagramLoadRequestHandler, ControlActionHandler, CanvasExportRequestedHandler {

    private static String PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";

    private static String CURSOR = "cursor: pointer;";
    private static float MAX_ZOOM = 8.0f;
    private static float MIN_ZOOM = 0.2f;

    private EventBus eventBus;
    private DiagramContext context;
    private RegExp regExp;

    private OMSVGSVGElement svg;
    private List<OMSVGElement> svgLayers;
    private OMSVGDefsElement defs;
    private OMSVGMatrix ctm;
    private OMSVGMatrix initialTM;
    private float zFactor = 1;

    private boolean isPanning;
    private boolean avoidClicking;

    private OMSVGPoint origin;

    private SVGLoader svgLoader;
    private StringBuilder sb;

    public SVGPanel(EventBus eventBus, int width, int height) {
        this.getElement().addClassName("pwp-SVGPanel");
        this.getElement().getStyle().setBackgroundColor("white");
        this.eventBus = eventBus;

        regExp = RegExp.compile(PATTERN);

        initFilters();
        initHandlers();
        sb = new StringBuilder();
        svgLoader = new SVGLoader(this);
        setSize(width, height);
    }

    public void load(String cPicture) {
        setVisible(false);
        svgLoader.load(cPicture);
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        switch (event.getAction()) {
            case FIT_ALL:       fitAll();                         break;
            case ZOOM_IN:       zoom(1.1f, getCentrePoint());     break;
            case ZOOM_OUT:      zoom(0.9f, getCentrePoint());     break;
            case UP:            translate(0, 10);                 break;
            case RIGHT:         translate(-10, 0);                break;
            case DOWN:          translate(0, -10);                break;
            case LEFT:          translate(10, 0);                 break;
//            case FIREWORKS:     overview();                       break;
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
        String cPic = context.getContent().getCPicture();
        if(cPic != null) {
            load(cPic);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault(); event.stopPropagation();
        origin = getTranslatedPoint(event);
        isPanning = true;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if(isPanning) {
            avoidClicking = true;
            OMSVGPoint end = getTranslatedPoint(event);

            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(end.getX() - origin.getX(), end.getY() - origin.getY());
            ctm = ctm.multiply(newMatrix);

            applyCTM();
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
        fitAll();
    }

    @Override
    public void onSvgLoaderError(Throwable exception) {
        Console.error("Error loading SVG...");
        setVisible(false); //fall back to the green boxes diagram
    }

    public void resetView() {
        zFactor = 1;
        ctm = initialTM;
        applyCTM();
    }

    public void setSize(int width, int height) {
        //Set the size of the panel
        setWidth(width + "px");
        setHeight(height + "px");

        //Set the size of the SVG
        if(svg != null) {
            svg.setWidth(Style.Unit.PX, width);
            svg.setHeight(Style.Unit.PX, height);
        }
    }

    private void applyCTM() {
        sb.setLength(0);
        sb.append("matrix(").append(ctm.getA()).append(",").append(ctm.getB()).append(",").append(ctm.getC()).append(",")
                .append(ctm.getD()).append(",").append(ctm.getE()).append(",").append(ctm.getF()).append(")");
        for (OMSVGElement svgLayer : svgLayers) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }
    }

    private void fitAll(){
        resetView();

        OMSVGRect bb = svg.getBBox();
        // Add a frame around the image
        float frame = 20;
        bb.setX(bb.getX() - frame);
        bb.setY(bb.getY() - frame);
        bb.setWidth(bb.getWidth() + (frame * 2));
        bb.setHeight(bb.getHeight() + (frame * 2));

        float rWidth = getOffsetWidth() / bb.getWidth();
        float rHeight = getOffsetHeight() / bb.getHeight();
        float zoom = (rWidth < rHeight) ? rWidth : rHeight;

        float vpCX = getOffsetWidth() * 0.5f;
        float vpCY = getOffsetHeight() * 0.5f;

        float newCX = bb.getX() + (bb.getWidth()  * 0.5f);
        float newCY = bb.getY() + (bb.getHeight() * 0.5f);

        float corX = vpCX/zoom - newCX;
        float corY = vpCY/zoom - newCY;

        OMSVGMatrix newMatrix = svg.createSVGMatrix().scale(zoom).translate(corX, corY);
        ctm = ctm.multiply(newMatrix);
        zFactor = zFactor * zoom;
        applyCTM();

    }

    private OMSVGPoint getCentrePoint() {
        OMSVGPoint p = svg.createSVGPoint();
        p.setX(getOffsetWidth()/2);
        p.setY(getOffsetHeight()/2);

        return p.matrixTransform(ctm.inverse());
    }

    private List<OMSVGElement> getRootLayers() {
        // Identify all layers by getting all top-level g elements
        List<OMSVGElement> svgLayers = new ArrayList<>();
        OMNodeList<OMNode> cNodes = svg.getChildNodes();
        for (OMNode node : cNodes) {
            if(node instanceof OMSVGGElement) {
                svgLayers.add((OMSVGGElement) node);
            }
        }
        return svgLayers;
    }

    private OMSVGPoint getTranslatedPoint(MouseEvent event) {
        OMSVGPoint p = svg.createSVGPoint();
        p.setX(event.getX());
        p.setY(event.getY());

        return p.matrixTransform(ctm.inverse());
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
    }

    private void translate(float x, float y) {
        OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(x, y);
        ctm = ctm.multiply(newMatrix);
        applyCTM();
    }

    private void zoom(float zoom, OMSVGPoint c) {
        if(zoom != 1 && (zFactor * zoom <= MAX_ZOOM) && (zFactor * zoom >= MIN_ZOOM)) {
            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(c.getX(), c.getY()).scale(zoom).translate(-c.getX(), -c.getY());
            ctm = ctm.multiply(newMatrix);
            zFactor = zFactor * zoom;
            applyCTM();
        }
    }
}
