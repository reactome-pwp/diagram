package org.reactome.web.diagram.util.svg;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
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
public class SVGPanel extends AbsolutePanel implements ClickHandler,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        SVGLoader.Handler, DatabaseObjectCreatedHandler {
    private EventBus eventBus;

    private static String PATTERN = "R-[A-Z]{3}-[0-9]{3,}(\\.[0-9]+)?";
    private static String CURSOR = "cursor: pointer;";
    private static float MAX_ZOOM = 12.0f;
    private static float MIN_ZOOM = 0.2f;

    private OMSVGSVGElement svg;
    private List<OMSVGElement> svgLayers;
    private OMSVGDefsElement defs;
    private OMSVGMatrix ctm;
    private OMSVGMatrix initialTM;
    private float zFactor = 1;

    private boolean isPanning;
    private OMSVGPoint origin;

    private SVGLoader svgLoader;

    public SVGPanel(EventBus eventBus, int width, int height) {
        //todo to be removed - only for debug purposes
        getElement().getStyle().setBackgroundColor("Green");

        this.eventBus = eventBus;
        this.getElement().addClassName("pwp-SVGPanel");
        initFilters();
        setSize(width, height);
        svgLoader = new SVGLoader(this);
        load("Apoptosis.svg");
    }

    public void load(String cPicture) {
        setVisible(false);
        svgLoader.load(cPicture);
    }

    @Override
    public void onClick(ClickEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        Console.info(el.getTagName() + ":" + el.getAttribute("id") + " was clicked!");
        DatabaseObjectFactory.get(el.getAttribute("id"), this);
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        Pathway p = (Pathway) databaseObject;
        eventBus.fireEventFromSource(new DiagramLoadRequestEvent(p), this);
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {

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
            OMSVGPoint end = getTranslatedPoint(event);

            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(end.getX() - origin.getX(), end.getY() - origin.getY());
            ctm = ctm.multiply(newMatrix);

            applyCTM();
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        isPanning = false;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        Console.info("Mouse over " + el.getTagName() + ":" + el.getAttribute("id"));
        OMSVGGElement gel = (OMSVGGElement) el;
        gel.getStyle().setSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl("shadowFilter"));
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.preventDefault(); event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        Console.info("Mouse out " + el.getTagName() + ":" + el.getAttribute("id"));
        OMSVGGElement gel = (OMSVGGElement) el;
        gel.getStyle().clearSVGProperty(SVGConstants.SVG_FILTER_ATTRIBUTE);
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault(); event.stopPropagation();
        float delta = event.getDeltaY() * 0.020f;
        float zoom = (1 - delta) > 0 ? (1 - delta) : 1;

        if(zoom != 1 && (zFactor * zoom <= MAX_ZOOM) && (zFactor * zoom >= MIN_ZOOM)) {
            OMSVGPoint p = getTranslatedPoint(event);
            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(p.getX(), p.getY()).scale(zoom).translate(-p.getX(), -p.getY());
            ctm = ctm.multiply(newMatrix);
            zFactor = zFactor * zoom;
            applyCTM();
        }
    }

    @Override
    public void onSvgLoaded(OMSVGSVGElement svg, long time) {
        setVisible(true);
        this.svg = svg;
        RegExp regExp = RegExp.compile(PATTERN);
        OMSVGGElement gElement = new OMSVGGElement();
        OMNodeList<OMElement> children = svg.getElementsByTagName(gElement.getTagName());
        for (OMElement child : children) {
            if(regExp.test(child.getId())) {
                Console.info(">>> " + child.getId() + "-" + child.getClass().getSimpleName());
                child.addDomHandler(SVGPanel.this, ClickEvent.getType());
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
        resetView();

    }

    @Override
    public void onSvgLoaderError(Throwable exception) {
        setVisible(false);
        Console.error("Error loading SVG");
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
        StringBuilder sb = new StringBuilder("matrix(");
        sb.append(ctm.getA()).append(",").append(ctm.getB()).append(",").append(ctm.getC()).append(",")
                .append(ctm.getD()).append(",").append(ctm.getE()).append(",").append(ctm.getF()).append(")");
        for (OMSVGElement svgLayer : svgLayers) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }
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


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
    }

    public interface Resources extends ClientBundle {

//        @Source("Test.svg")
//        ExternalSVGResource test();
//
//        @Source("Apoptosis.svg")
//        ExternalSVGResource apoptosis();

    }
}
