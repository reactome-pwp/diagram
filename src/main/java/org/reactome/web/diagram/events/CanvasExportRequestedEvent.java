package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.CanvasExportRequestedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class CanvasExportRequestedEvent extends GwtEvent<CanvasExportRequestedHandler> {
    public static Type<CanvasExportRequestedHandler> TYPE = new Type<>();

    public enum Option {
        IMAGE,
        PPTX
    }

    private Option option;

    public CanvasExportRequestedEvent(Option option) {
        this.option = option;
    }

    @Override
    public Type<CanvasExportRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CanvasExportRequestedHandler handler) {
        handler.onDiagramExportRequested(this);
    }

    public Option getOption() {
        return option;
    }

    @Override
    public String toString() {
        return "CanvasExportRequestedEvent{" +
                "option=" + option +
                '}';
    }
}
