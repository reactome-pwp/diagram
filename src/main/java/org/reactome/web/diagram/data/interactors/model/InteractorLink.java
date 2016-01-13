package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLink extends DiagramInteractor {

    final Node from;
    Coordinate fromCentre;

    private String id;
    private double score;
    private boolean visible = true;

    InteractorLink(Node from, String id, double score) {
        this.from = from;
        this.id = id;
        this.score = score;
    }

    public Coordinate getFrom() {
        return fromCentre;
    }

    public abstract Coordinate getTo();

    public abstract String getToAccession();

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public void setBoundaries(Coordinate to) {
        Segment link = SegmentFactory.get(InteractorsLayout.getCentre(from.getProp()), to);
        fromCentre = InteractorsLayout.getSegmentsIntersection(link, from);

        minX = Math.min(fromCentre.getX(), to.getX());
        maxX = Math.max(fromCentre.getX(), to.getX());
        minY = Math.min(fromCentre.getY(), to.getY());
        maxY = Math.max(fromCentre.getY(), to.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractorLink that = (InteractorLink) o;

        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
