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
    private Pathway subpathway;

    public DiagramLoadRequestEvent(Pathway pathway) {
        this.pathway = pathway;
    }

    public DiagramLoadRequestEvent(Pathway pathway, Pathway subpathway) {
        this.pathway = pathway;
        this.subpathway = subpathway;
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

    public Pathway getSubpathway() {
        return subpathway;
    }

    @Override
    public String toString() {
        return "DiagramLoadRequestEvent{" +
                "pathway=" + pathway.getIdentifier() +
                (subpathway == null ? "" : ", subpathway=" + subpathway.getIdentifier()) +
                '}';
    }
}
