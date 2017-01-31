package org.reactome.web.diagram.client.visualisers.ehld.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.client.visualisers.ehld.handlers.SVGPanZoomHandler;
import org.vectomatic.dom.svg.OMSVGPoint;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public class SVGPanZoomEvent extends GwtEvent<SVGPanZoomHandler> {
    public static Type<SVGPanZoomHandler> TYPE = new Type<>();

    private OMSVGPoint from;
    private OMSVGPoint to;

    public SVGPanZoomEvent(OMSVGPoint from, OMSVGPoint to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Type<SVGPanZoomHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SVGPanZoomHandler handler) {
        handler.onSVGPanZoom(this);
    }

    public OMSVGPoint getFrom() {
        return from;
    }

    public OMSVGPoint getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "SVGPanZoomEvent{" +
                "from:{x:" + from.getX() + ", y:" + from.getY() + "}" +
                ", to:{x:" + to.getX() + ", y:" + to.getY() + "}" +
                '}';
    }
}
