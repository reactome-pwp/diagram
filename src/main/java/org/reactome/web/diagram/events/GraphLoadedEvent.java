package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.handlers.GraphLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphLoadedEvent extends GwtEvent<GraphLoadedHandler> {
    public static Type<GraphLoadedHandler> TYPE = new Type<>();

    private DiagramContent content;
    private long time;

    public GraphLoadedEvent(DiagramContent content, long time) {
        this.content = content;
        this.time = time;
    }

    @Override
    public Type<GraphLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GraphLoadedHandler handler) {
        handler.onGraphLoaded(this);
    }

    public DiagramContent getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "GraphLoadedEvent{" +
                "time=" + time +
                ", content=" + content +
                '}';
    }
}
