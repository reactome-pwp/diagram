package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
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

    public String getAccession(){
        GraphPhysicalEntity pe = to.getGraphObject();
        return pe.getIdentifier();
    }

    @Override
    public Coordinate getCoordinateTo() {
        return toCentre;
    }

    @Override
    public String getToAccession() {
        GraphPhysicalEntity pe = to.getGraphObject();
        return pe.getIdentifier();
    }

    public Node getNodeTo(){
        return to;
    }

    @Override
    public void setBoundaries(Coordinate to) {
        Segment link = SegmentFactory.get(InteractorsLayout.getCentre(from.getProp()), to);
        fromPoint = InteractorsLayout.getSegmentsIntersection(link, from);
        toCentre = InteractorsLayout.getSegmentsIntersection(link, this.to);

        minX = Math.min(fromPoint.getX(), toCentre.getX());
        maxX = Math.max(fromPoint.getX(), toCentre.getX());
        minY = Math.min(fromPoint.getY(), toCentre.getY());
        maxY = Math.max(fromPoint.getY(), toCentre.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StaticLink that = (StaticLink) o;

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