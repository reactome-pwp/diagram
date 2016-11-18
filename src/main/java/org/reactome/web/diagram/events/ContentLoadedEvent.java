package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContentLoadedEvent extends GwtEvent<ContentLoadedHandler> {

    public static Type<ContentLoadedHandler> TYPE = new Type<>();

    private DiagramContext context;

    public ContentLoadedEvent(DiagramContext context) {
        this.context = context;
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

    @Override
    public String toString() {
        return "ContentLoadedEvent{" +
                "context=" + context +
                "type=" + context.getContent().getType() +
                '}';
    }
}
