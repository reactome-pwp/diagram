package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagRequestHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramObjectsFlagRequestedEvent extends GwtEvent<DiagramObjectsFlagRequestHandler> {
    public static final Type<DiagramObjectsFlagRequestHandler> TYPE = new Type<>();

    private String term;

    public DiagramObjectsFlagRequestedEvent(String term) {
        this.term = term;
    }

    @Override
    public Type<DiagramObjectsFlagRequestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramObjectsFlagRequestHandler handler) {
        handler.onDiagramObjectsFlagRequested(this);
    }

    public String getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "DiagramObjectsFlagRequestEvent{" +
                "term='" + term + '\'' +
                '}';
    }
}
