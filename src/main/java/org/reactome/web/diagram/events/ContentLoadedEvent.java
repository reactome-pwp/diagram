package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContentLoadedEvent extends GwtEvent<ContentLoadedHandler> {

    public enum Content {DIAGRAM, SVG}

    public static Type<ContentLoadedHandler> TYPE = new Type<>();

    private DiagramContext context;
    private OMSVGSVGElement svg;
    public final Content CONTENT_TYPE;

    public ContentLoadedEvent(DiagramContext context) {
        CONTENT_TYPE = Content.DIAGRAM;
        this.context = context;
    }

    public ContentLoadedEvent(OMSVGSVGElement svg) {
        this.CONTENT_TYPE = Content.SVG;
        this.svg = svg;
    }

    @Override
    public Type<ContentLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ContentLoadedHandler handler) {
        handler.onContentLoaded(this);
    }

    public DiagramContext getContext() {
        return context;
    }

    public OMSVGSVGElement getSVG() {
        return svg;
    }

    @Override
    public String toString() {
        return "ContentLoadedEvent{" +
                "context=" + context +
                "type=" + CONTENT_TYPE +
                '}';
    }
}
