package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorLink extends DiagramInteractor {

    Node from;
    Coordinate fromCentre;

    public InteractorLink(Node from) {
        this.from = from;
        fromCentre = getCentre(from);
    }

    public Coordinate getFrom() {
        return fromCentre;
    }

    public abstract Coordinate getTo();

    Coordinate getCentre(QuadTreeBox box) {
        return CoordinateFactory.get(
                box.getMinX() + (box.getMaxX() - box.getMinX()) / 2.0,
                box.getMinY() + (box.getMaxY() - box.getMinY()) / 2.0
        );
    }

    void setBoundaries(QuadTreeBox from, QuadTreeBox to) {
        minX = Math.min(from.getMinX(), to.getMinX());
        minY = Math.min(from.getMinY(), to.getMinY());
        maxX = Math.max(from.getMaxX(), to.getMaxX());
        maxY = Math.max(from.getMaxY(), to.getMaxY());
    }
}
