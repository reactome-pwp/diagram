package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class StaticLink extends InteractorLink {

    private Node to;
    private Coordinate toCentre;

    public StaticLink(Node from, Node to) {
        super(from);
        this.to = to;
        toCentre = getCentre(to);
        setBoundaries(from, to);
    }

    @Override
    public Coordinate getTo() {
        return toCentre;
    }
}