package org.reactome.web.diagram.util.svg.thumbnail;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.events.DiagramLoadRequestEvent;
import org.reactome.web.diagram.events.ViewportResizedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;
import org.reactome.web.diagram.handlers.ViewportResizedHandler;
import org.reactome.web.diagram.util.svg.AbstractSVGPanel;
import org.reactome.web.diagram.util.svg.events.SVGLoadedEvent;
import org.reactome.web.diagram.util.svg.events.SVGPanZoomEvent;
import org.reactome.web.diagram.util.svg.handlers.SVGLoadedHandler;
import org.reactome.web.diagram.util.svg.handlers.SVGPanZoomHandler;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGRect;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.SVGConstants;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGThumbnail extends AbstractSVGPanel implements DiagramLoadRequestHandler, SVGLoadedHandler,
        SVGPanZoomHandler, ViewportResizedHandler {
    private static final int HEIGHT = 75;
    private static final int FALLBACK_WIDTH = 125;

    private Canvas frame;

    public SVGThumbnail(EventBus eventBus) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGThumbnail");

        this.frame = this.createCanvas(0, 0);

        this.setStyle();
        this.initHandlers();
    }

    @Override
    public void onDiagramLoadRequest(DiagramLoadRequestEvent event) {

    }

    @Override
    public void onSVGLoaded(SVGLoadedEvent event) {
        svg = (OMSVGSVGElement) event.getSVG().cloneNode(true);

        OMSVGRect svgSize = getSVGInitialSize();
        double factor = HEIGHT / (svgSize.getHeight() + FRAME);
        setSize((int) Math.ceil(factor * (svgSize.getWidth() + FRAME)), HEIGHT);

        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);

        Element div = getElement();
        if(div.getChildCount() == 1) {
            // Only the canvas is added
            div.insertFirst(svg.getElement());
        } else {
            // both the canvas and the svg have been added
            div.replaceChild(svg.getElement(), div.getFirstChild());
        }

        // Identify all layers by getting all top-level g elements
        svgLayers = getRootLayers();

        // Set initial translation matrix
        initialTM = svg.getCTM();
        initialBB = svg.getBBox();

//        ctm = initialTM;
        OMSVGMatrix fitTM = calculateFitAll(FRAME);

        ctm = initialTM.multiply(fitTM);
        applyCTM(false);
    }

    @Override
    public void onSVGPanZoom(SVGPanZoomEvent event) {
        drawFrame(event.getFrom(), event.getTo());
    }

    @Override
    public void onViewportResized(ViewportResizedEvent event) {
        //todo Implement this
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        frame.setCoordinateSpaceWidth(width);
        frame.setCoordinateSpaceHeight(height);
        frame.setPixelSize(width, height);

        Context2d ctx = this.frame.getContext2d();
        ctx.setStrokeStyle("#000000");
        ctx.setFillStyle("rgba(200,200,200, 0.4)");
        ctx.setLineWidth(0.5);
    }

    private void cleanFrame() {
        frame.getContext2d().clearRect(0, 0, frame.getOffsetWidth(), frame.getOffsetHeight());
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 0);
        return canvas;
    }

    private void drawFrame(OMSVGPoint from, OMSVGPoint to) {
        OMSVGPoint f = from.matrixTransform(this.ctm);
        OMSVGPoint t = to.matrixTransform(this.ctm);

        float tX = f.getX();
        float tY = f.getY();

        float W = t.getX() - tX;
        float H =t.getY() - tY;

        cleanFrame();

        Context2d ctx = this.frame.getContext2d();
        ctx.fillRect(0, 0, this.frame.getOffsetWidth(), this.frame.getOffsetHeight());
        ctx.clearRect(tX, tY, W, H);
        ctx.strokeRect(tX, tY, W, H);
    }

    private OMSVGRect getSVGInitialSize() {
        return svg.getViewBox().getBaseVal()!=null ? svg.getViewBox().getBaseVal() : svg.createSVGRect(0, 0, FALLBACK_WIDTH, HEIGHT);
    }

    private void initHandlers() {
        eventBus.addHandler(DiagramLoadRequestEvent.TYPE, this);
        eventBus.addHandler(SVGLoadedEvent.TYPE, this);
        eventBus.addHandler(SVGPanZoomEvent.TYPE, this);
//        eventBus.addHandler(ViewportResizedEvent.TYPE, this);
    }

    private void setStyle() {
        Style style = this.getElement().getStyle();
        style.setBackgroundColor("blue");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
    }

}
