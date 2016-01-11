package org.reactome.web.diagram.data.interactors.model;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntity extends DiagramInteractor implements Draggable {

    private String accession;

    Set<GraphPhysicalEntity> interactsWith = new HashSet<>();
    Set<InteractorLink> links = new HashSet<>();

    public InteractorEntity(String accession) {
        this.accession = accession;
    }

    public Set<InteractorLink> addInteraction(DiagramObject item, String id, double score) {
        //IMPORTANT: local set is meant to return ONLY the new ones
        Set<InteractorLink> interactors = new HashSet<>();
        GraphPhysicalEntity pe = item.getGraphObject();
        interactsWith.add(pe);

        DynamicLink link = new DynamicLink((Node) item, this, id, score);
        interactors.add(link);
        this.links.add(link);
        return interactors;
    }

    public Set<GraphPhysicalEntity> getInteractsWith() {
        return interactsWith;
    }

    public String getAccession() {
        return accession;
    }

    public Coordinate getCentre(){
        return CoordinateFactory.get(
                minX + (maxX - minX) / 2.0,
                minY + (maxY - minY) / 2.0
        );
    }

    public boolean isLaidOut(){
        return minX != null && maxX != null && minY != null && maxY !=null;
    }

    @Override
    public boolean isVisible() {
        for (InteractorLink link : links) {
            if(link.isVisible()) return true;
        }
        return false;
    }

    public Set<InteractorLink> getLinks() {
        return links;
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

    @Override
    public void setVisible(boolean visible) {
        throw new RuntimeException("Do not use this method. Please rely on the visibility of the links");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InteractorEntity entity = (InteractorEntity) o;

        return accession != null ? accession.equals(entity.accession) : entity.accession == null;

    }

    @Override
    public int hashCode() {
        return accession != null ? accession.hashCode() : 0;
    }
}
