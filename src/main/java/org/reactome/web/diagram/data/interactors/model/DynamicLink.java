package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DynamicLink extends InteractorLink {

    private InteractorEntity to;

    public DynamicLink(Node from, InteractorEntity to, String id, double score) {
        super(from, id, score);
        this.to = to;
        setBoundaries(to.getCentre());
    }

    @Override
    public Coordinate getTo() {
        return to.getCentre(); //This needs to be calculated every time since InteractorEntity is Draggable
    }

    @Override
    public String getToAccession() {
        return to.getAccession();
    }

    public InteractorEntity getInteractorEntity(){
        return to;
    }
}
