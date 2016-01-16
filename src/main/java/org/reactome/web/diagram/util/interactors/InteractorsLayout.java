package org.reactome.web.diagram.util.interactors;

import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLayout {

    private static final double L = 2 * Math.PI;
    private static final double OFFSET = Math.PI / 2.0;

    private static final int BOX_WIDTH = 50;
    private static final int BOX_HEIGHT = 25;
    private static final int RADIUS = 200;

    private Node node;

    public InteractorsLayout(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public boolean doLayout(InteractorEntity entity, int i, int n) {
        return doLayout(node, entity, i, n, !entity.isVisible());
    }

    public static boolean doLayout(Node node, InteractorEntity entity, int i, int n, boolean force) {
        if (entity == null || n == 0 || i < 0 || i > n) return false;

        if (force || !entity.isLaidOut()) {
            double delta = L / (double) n;
            double angle = delta * i + OFFSET;
            Coordinate center = getCentre(node.getProp());

            double x = center.getX() - RADIUS * Math.cos(angle);
            double y = center.getY() - RADIUS * Math.sin(angle);

            entity.setMinX(x - BOX_WIDTH);
            entity.setMaxX(x + BOX_WIDTH);
            entity.setMinY(y - BOX_HEIGHT);
            entity.setMaxY(y + BOX_HEIGHT);
            return true;
        }
        return false;
    }

    public static Coordinate getCentre(NodeProperties prop) {
        return CoordinateFactory.get(
                prop.getX() + prop.getWidth() / 2.0,
                prop.getY() + prop.getHeight() / 2.0
        );
    }

    public static Coordinate getSegmentsIntersection(Segment interactor, Node node) {
        NodeProperties prop = node.getProp();
        Coordinate a = CoordinateFactory.get(prop.getX(), prop.getY());
        Coordinate b = CoordinateFactory.get(prop.getX() + prop.getWidth(), prop.getY());
        Coordinate c = CoordinateFactory.get(prop.getX() + prop.getWidth(), prop.getY() + prop.getHeight());
        Coordinate d = CoordinateFactory.get(prop.getX(), prop.getY() + prop.getHeight());

        List<Segment> segments = new LinkedList<>();
        segments.add(SegmentFactory.get(a, b));
        segments.add(SegmentFactory.get(b, c));
        segments.add(SegmentFactory.get(c, d));
        segments.add(SegmentFactory.get(d, a));

        for (Segment segment : segments) {
            Coordinate point = getSegmentsIntersection(segment, interactor);
            if (point != null) return point;
        }
        //By default the centre is retrieved...
        return getCentre(node.getProp());
    }

    private static Coordinate getSegmentsIntersection(Segment s1, Segment s2) {
        Coordinate from1 = s1.getFrom();
        Coordinate to1 = s1.getTo();
        Coordinate from2 = s2.getFrom();
        Coordinate to2 = s2.getTo();
        return intersection(from1.getX(), from1.getY(), to1.getX(), to1.getY(), from2.getX(), from2.getY(), to2.getX(), to2.getY());
    }

    private static Coordinate intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        if (!doesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) return null;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0) return null;

        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return CoordinateFactory.get(xi, yi);
    }

    //Code adapted from http://www.java-gaming.org/index.php?topic=22590.0
    public static boolean doesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));

        if (d == 0.0f) return false;

        double ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
        double ub = (((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3))) / d;

        return ((ua >= 0.0d) && (ua <= 1.0d) && (ub >= 0.0d) && (ub <= 1.0d));
    }
}
