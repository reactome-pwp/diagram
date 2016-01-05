package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DynamicLink extends InteractorLink {

    private InteractorEntity to;
    private String id;
    private double score;

    public DynamicLink(Node from, InteractorEntity to, String id, double score) {
        super(from);
        this.to = to;
        this.id = id;
        this.score = score;
        setBoundaries(from, to);
    }

    @Override
    public Coordinate getTo() {
        return getCentre(to); //This needs to be calculated every time since InteractorEntity is Draggable
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }
}
