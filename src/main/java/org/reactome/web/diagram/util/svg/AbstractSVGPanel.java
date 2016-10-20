package org.reactome.web.diagram.util.svg;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.context.popups.ImageDownloadDialog;
import org.reactome.web.diagram.util.svg.events.SVGPanZoomEvent;
import org.reactome.web.diagram.util.svg.filters.FilterColour;
import org.reactome.web.diagram.util.svg.filters.FilterFactory;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class AbstractSVGPanel extends AbsolutePanel {
    protected EventBus eventBus;

    protected static final float FRAME = 40;
    protected static final String HOVERING_FILTER = "shadowFilter";
    protected static final String SELECTION_FILTER = "selectionFilter";
    protected static final String COMBINED_FILTER = "combinedFilter";
    protected static final String HOVERING_OVERLAY_FILTER = "hoveringOverlayFilter";
    protected static final String SELECTION_OVERLAY_FILTER = "selectionOverlayFilter";

    protected OMSVGSVGElement svg;
    protected List<OMSVGElement> svgLayers;
    protected Map<String, SVGEntity> entities;

    protected OMSVGDefsElement baseDefs;

    protected OMSVGMatrix ctm;
    protected OMSVGMatrix initialTM;
    protected OMSVGRect initialBB;
    protected float zFactor = 1;

    private StringBuilder sb;

    public AbstractSVGPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        sb = new StringBuilder();
        initFilters();
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

    protected void applyCTM(boolean fireEvent) {
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

    protected OMSVGMatrix calculateFitAll(final float frame){
        OMSVGRect bb = svg.createSVGRect();
        // Add a frame around the image
        bb.setX(initialBB.getX() - frame);
        bb.setY(initialBB.getY() - frame);
        bb.setWidth(initialBB.getWidth() + (frame * 2));
        bb.setHeight(initialBB.getHeight() + (frame * 2));

        float rWidth = getOffsetWidth() / bb.getWidth();
        float rHeight = getOffsetHeight() / bb.getHeight();
        float zoom = (rWidth < rHeight) ? rWidth : rHeight;

        float vpCX = getOffsetWidth() * 0.5f;
        float vpCY = getOffsetHeight() * 0.5f;

        float newCX = bb.getX() + (bb.getWidth()  * 0.5f);
        float newCY = bb.getY() + (bb.getHeight() * 0.5f);

        float corX = vpCX/zoom - newCX;
        float corY = vpCY/zoom - newCY;

        return svg.createSVGMatrix().scale(zoom).translate(corX, corY);
    }

    protected void exportSVG(String stableId){
        if(svg != null) {
            Image image = new Image();
            image.setUrl("data:image/svg+xml," + svg.getMarkup());
            final ImageDownloadDialog downloadDialogBox = new ImageDownloadDialog(image, "svg", stableId);
            downloadDialogBox.show();
        }
    }

    protected OMSVGPoint getCentrePoint() {
        return getTranslatedPoint(getOffsetWidth()/2, getOffsetHeight()/2);
    }

    protected OMSVGMatrix getInitialCTM() {
        return svg.createSVGMatrix(1, 0, 0, 1, 0, 0);
    }

    protected OMSVGPoint getTranslatedPoint(MouseEvent event) {
        return getTranslatedPoint(event.getX(), event.getY());
    }

    protected OMSVGPoint getTranslatedPoint(int x, int y) {
        OMSVGPoint p = svg.createSVGPoint();
        p.setX(x); p.setY(y);
        return p.matrixTransform(ctm.inverse());
    }

    protected List<OMSVGElement> getRootLayers() {
        // Identify all layers by getting all top-level <g> elements
        List<OMSVGElement> svgLayers = new ArrayList<>();
        OMNodeList<OMNode> cNodes = svg.getChildNodes();
        for (OMNode node : cNodes) {
            if(node instanceof OMSVGGElement) {
                svgLayers.add((OMSVGGElement) node);
            }
        }
        return svgLayers;
    }

    protected String hex2Rgb(String colorStr, float alpha) {
        int r = Integer.valueOf( colorStr.substring( 1, 3 ), 16 );
        int g = Integer.valueOf( colorStr.substring( 3, 5 ), 16 );
        int b = Integer.valueOf( colorStr.substring( 5, 7 ), 16 );
        float a = alpha >= 0 && alpha <= 1 ? alpha : 1.0f;
        return "rgba(" + r + "," + g + "," + b + "," + NumberFormat.getFormat("#.#").format(a) + ")";
    }

    protected void initFilters() {
        baseDefs = new OMSVGDefsElement();
        baseDefs.appendChild(FilterFactory.getShadowFilter(HOVERING_FILTER));
        baseDefs.appendChild(FilterFactory.getOutlineFilter(SELECTION_FILTER, FilterColour.BLUE));
        baseDefs.appendChild(FilterFactory.getShadowWithOutlineFilter(COMBINED_FILTER, FilterColour.BLUE));
//        baseDefs.appendChild(FilterFactory.combine(COMBINED_FILTER, FilterFactory.getShadowFilter(HOVERRING_FILTER), FilterFactory.getOutlineFilter(SELECTION_FILTER, FilterColour.BLUE)));
        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(HOVERING_OVERLAY_FILTER, FilterColour.YELLOW));
        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(SELECTION_OVERLAY_FILTER, FilterColour.BLUE));
    }
    protected void notifyAboutChangeInView() {
        if(svg != null && ctm !=null) {
            OMSVGPoint from = svg.createSVGPoint(0, 0);
            from = from.matrixTransform(ctm.inverse());

            OMSVGPoint to = svg.createSVGPoint(getOffsetWidth(), getOffsetHeight());
            to = to.matrixTransform(ctm.inverse());

            eventBus.fireEventFromSource(new SVGPanZoomEvent(from, to), this);
        }
    }

    protected void removeAttributeFromChildren(final OMNode root, final String attribute) {
        Iterator<OMNode> it = root.getChildNodes().iterator();
        while (it.hasNext()) {
            OMNode c = it.next();
            if(c instanceof OMElement) {
                OMElement el = (OMElement) c;
                el.removeAttribute(attribute);
                removeAttributeFromChildren(c, attribute);
            }
        }
    }

    protected OMNodeList<OMElement> getAllTextElementsFrom(final OMNode root){
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> textEl = el.getElementsByTagName("text");
        return textEl;
    }
}
