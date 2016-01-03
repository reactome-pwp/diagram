package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DynamicLink extends InteractorLink {

    private InteractorEntity to;

    public DynamicLink(Node from, InteractorEntity to) {
        super(from);
        this.to = to;
        setBoundaries(from, to);
    }

    @Override
    public Coordinate getTo() {
        return getCentre(to); //This needs to be calculated every time since InteractorEntity is Draggable
    }
}
