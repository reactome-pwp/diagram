package org.reactome.web.diagram.util.svg.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.util.svg.handlers.SVGLoadedHandler;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGLoadedEvent extends GwtEvent<SVGLoadedHandler> {
    public static Type<SVGLoadedHandler> TYPE = new Type<>();

    private OMSVGSVGElement svg;
    private long time;

    public SVGLoadedEvent(OMSVGSVGElement svg, long time) {
        this.svg = svg;
        this.time = time;
    }

    @Override
    public Type<SVGLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SVGLoadedHandler handler) {
        handler.onSVGLoaded(this);
    }

    public OMSVGSVGElement getSVG() {
        return svg;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SVGLoadedEvent{" +
                "svg=" + svg.getId() +
                ", time=" + time +
                '}';
    }
}

