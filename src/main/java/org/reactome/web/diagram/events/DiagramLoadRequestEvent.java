package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramLoadRequestEvent extends GwtEvent<DiagramLoadRequestHandler> {
    public static final Type<DiagramLoadRequestHandler> TYPE = new Type<>();

    private String identifier;

    public DiagramLoadRequestEvent(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Type<DiagramLoadRequestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramLoadRequestHandler handler) {
        handler.onDiagramLoadRequest(this);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "DiagramLoadRequestEvent{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
