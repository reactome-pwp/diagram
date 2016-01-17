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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DynamicLink that = (DynamicLink) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return to != null ? to.equals(that.to) : that.to == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }
}
