package org.reactome.web.diagram.data.interactors.common;

import org.reactome.web.diagram.data.layout.Coordinate;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorBox implements QuadTreeBox {
    protected double minX, minY, maxX, maxY;

    public InteractorBox(QuadTreeBox box) {
        this.minX = box.getMinX();
        this.minY = box.getMinY();
        this.maxX = box.getMaxX();
        this.maxY = box.getMaxY();
    }

    public InteractorBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public InteractorBox transform(double factor, Coordinate delta) {
        return new InteractorBox(
                minX * factor + delta.getX(),
                minY * factor + delta.getY(),
                maxX * factor + delta.getX(),
                maxY * factor + delta.getY()
        );
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }
}
