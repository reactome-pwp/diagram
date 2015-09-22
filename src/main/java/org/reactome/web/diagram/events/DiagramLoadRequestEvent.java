package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramLoadRequestHandler;
import org.reactome.web.pwp.model.classes.Pathway;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramLoadRequestEvent extends GwtEvent<DiagramLoadRequestHandler> {
    public static final Type<DiagramLoadRequestHandler> TYPE = new Type<>();

    private Pathway pathway;

    public DiagramLoadRequestEvent(Pathway pathway) {
        this.pathway = pathway;
    }

    @Override
    public Type<DiagramLoadRequestHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramLoadRequestHandler handler) {
        handler.onDiagramLoadRequest(this);
    }

    public Pathway getPathway() {
        return pathway;
    }

    @Override
    public String toString() {
        return "DiagramLoadRequestEvent{" +
                "pathway=" + pathway.getIdentifier() +
                '}';
    }
}
