package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class StaticLink extends InteractorLink {

    private Node to;
    private Coordinate toCentre;

    public StaticLink(Node from, Node to, String id, double score) {
        super(from, id, score);
        this.to = to;
        toCentre = InteractorsLayout.getCentre(to.getProp());
        setBoundaries(toCentre);
    }

    @Override
    public Coordinate getTo() {
        return toCentre;
    }

    @Override
    public String getToAccession() {
        GraphPhysicalEntity pe = to.getGraphObject();
        return pe.getIdentifier();
    }

    public Node getNode(){
        return to;
    }
}