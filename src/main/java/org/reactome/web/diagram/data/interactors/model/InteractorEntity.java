package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntity extends DiagramInteractor implements Draggable {

    private String accession;
    private String id;
    private double score;

    Set<GraphPhysicalEntity> interactsWith = new HashSet<>();


    public InteractorEntity(RawInteractor interactor) {
        this.accession = interactor.getAcc();
        this.id = interactor.getId();
        this.score = interactor.getScore();
    }

    public Set<InteractorLink> addInteraction(GraphPhysicalEntity pe) {
        pe.addInteractor(this);

        //Bad idea doing this here since we don't yet know where is going to be placed in the viewport (no layout data)
        Set<InteractorLink> interactors = new HashSet<>();
        for (DiagramObject diagramObject : pe.getDiagramObjects()) {
            if (diagramObject instanceof Node) {
                interactors.add(new DynamicLink((Node) diagramObject, this));
            }
        }
        return interactors;
    }

    public String getAccession() {
        return accession;
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    @Override
    public void setMinX(double minX) {
        this.minX = minX;
    }

    @Override
    public void setMinY(double minY) {
        this.minY = minY;
    }

    @Override
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    @Override
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }
}
