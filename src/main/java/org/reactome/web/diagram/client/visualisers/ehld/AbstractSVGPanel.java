package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.client.visualisers.ehld.filters.FilterColour;
import org.reactome.web.diagram.client.visualisers.ehld.filters.FilterFactory;
import org.reactome.web.diagram.context.popups.ImageDownloadDialog;
import org.vectomatic.dom.svg.*;

import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class AbstractSVGPanel extends AbsolutePanel {
    protected EventBus eventBus;

    protected static final float FRAME = 40;
    protected static final String HOVERING_FILTER = "hoveringFilter";
    protected static final String SELECTION_FILTER = "selectionFilter";
    protected static final String FLAGGING_FILTER = "flaggingFilter";

    protected static final String FLAGGING_HOVERING_FILTER = "flaggingHoveringFilter";
    protected static final String SELECTION_HOVERING_FILTER = "selectionHoveredFilter";
    protected static final String SELECTION_FLAGGING_FILTER = "selectionFlaggingFilter";

    protected static final String SELECTION_FLAGGING_HOVERING_FILTER = "selectionFlaggingHoveringFilter";

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

    protected StringBuilder sb;

    public AbstractSVGPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        getElement().getStyle().setBackgroundColor("white");
        initFilters();
        sb = new StringBuilder();
    }

    public void exportView(String stableId){
        if(svg != null) {
            Image image = new Image();
            String base64 = btoa(svg.getMarkup());
            image.setUrl("data:image/svg+xml;base64," + base64);
            final ImageDownloadDialog downloadDialogBox = new ImageDownloadDialog(image, "svg", stableId);
            downloadDialogBox.show();
        }
    }

    native String btoa(String b64) /*-{
        return btoa(b64);
    }-*/;

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
        baseDefs.appendChild(FilterFactory.getOutlineFilter(FLAGGING_FILTER, FilterColour.CYAN));

        baseDefs.appendChild(FilterFactory.getShadowWithOutlineFilter(SELECTION_HOVERING_FILTER, FilterColour.BLUE));
        baseDefs.appendChild(FilterFactory.getShadowWithOutlineFilter(FLAGGING_HOVERING_FILTER, FilterColour.CYAN));
        baseDefs.appendChild(FilterFactory.getDoubleOutlineFilter(SELECTION_FLAGGING_FILTER, FilterColour.BLUE, FilterColour.CYAN));
        baseDefs.appendChild(FilterFactory.getShadowWithDoubleOutlineFilter(SELECTION_FLAGGING_HOVERING_FILTER, FilterColour.BLUE, FilterColour.CYAN));

        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(HOVERING_OVERLAY_FILTER, FilterColour.YELLOW));
        baseDefs.appendChild(FilterFactory.getColouredOverlayFilter(SELECTION_OVERLAY_FILTER, FilterColour.BLUE));
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

    protected List<OMElement> getAllTextElementsFrom(final OMNode root){
        List<OMElement> rtn = new LinkedList<>();
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> textEl = el.getElementsByTagName("text");
        for (OMElement element : textEl) {
            rtn.add(element);
        }
        return rtn;
    }

    protected boolean removeLogoFrom(final OMNode root){
        boolean rtn = false;
        OMElement el = (OMElement) root;
        OMNodeList<OMElement> targetEl = el.getElementsByTagName("g");
        for (OMElement element : targetEl) {
            if(element.getId().toLowerCase().startsWith("logo")) {
                element.getElement().removeFromParent();
                rtn = true;
            }
        }
        return rtn;
    }
}
