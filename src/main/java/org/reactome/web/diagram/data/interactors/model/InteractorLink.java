package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.category.SegmentCategory;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLink extends DiagramInteractor {

    final Node from;
    Coordinate fromPoint;

    private String id;
    private double score;
    private boolean visible = true;

    InteractorLink(Node from, String id, double score) {
        this.from = from;
        this.id = id;
        this.score = score;
    }

    public Coordinate getFrom() {
        return fromPoint;
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
        fromPoint = InteractorsLayout.getSegmentsIntersection(link, from);

        minX = Math.min(fromPoint.getX(), to.getX());
        maxX = Math.max(fromPoint.getX(), to.getX());
        minY = Math.min(fromPoint.getY(), to.getY());
        maxY = Math.max(fromPoint.getY(), to.getY());
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
    public boolean isHovered(Coordinate pos) {
        return SegmentCategory.isInSegment(SegmentFactory.get(fromPoint, getTo()), pos);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "InteractorLink{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", visible=" + isVisible() +
                ", score=" + score +
                ", id='" + id + '\'' +
                '}';
    }
}
