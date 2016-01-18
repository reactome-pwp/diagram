package org.reactome.web.diagram.data.interactors.common;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorBox implements QuadTreeBox {
    protected double minX, minY, maxX, maxY, width, height;

    public InteractorBox(QuadTreeBox box) {
        this(box.getMinX(), box.getMinY(), box.getMaxX(), box.getMaxY());;
    }

    public InteractorBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.width = Math.abs(maxX - minX);
        this.height = Math.abs(maxY - minY);
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Coordinate getCentre() {
       return CoordinateFactory.get(minX + width/2, minY + height/2);
    }
}
