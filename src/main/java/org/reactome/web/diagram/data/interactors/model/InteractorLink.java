package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.category.SegmentCategory;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLink extends DiagramInteractor implements Comparable<InteractorLink> {

    final Node from;
    Coordinate fromPoint;

    String id;
    double score;
    boolean visible = true;

    InteractorLink(Node from, String id, double score) {
        this.from = from;
        this.id = id;
        this.score = score;
    }

    public Node getNodeFrom() {
        return from;
    }

    public Coordinate getCoordinateFrom() {
        return fromPoint;
    }

    public abstract Coordinate getCoordinateTo();

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
    public boolean isHovered(Coordinate pos) {
        return SegmentCategory.isInSegment(SegmentFactory.get(fromPoint, getCoordinateTo()), pos);
    }

    @Override
    public boolean isVisible() {
        double threshold = InteractorsContent.getInteractorsThreshold(LoaderManager.INTERACTORS_RESOURCE);
        return visible && score >= threshold;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int compareTo(InteractorLink o) {
        int n = Double.compare(o.score, score);
        if (n == 0) return getToAccession().compareTo(o.getToAccession());
        return n;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
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
