package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.interactors.raw.Interactor;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DiagramInteractor implements QuadTreeBox {

    protected double minX, minY, maxX, maxY;

    DiagramInteractor() {
    }

    public DiagramInteractor(Interactor interactor) {
        this.minX = interactor.getMinX();
        this.minY = interactor.getMinY();
        this.maxX = interactor.getMaxX();
        this.maxY = interactor.getMaxY();
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
