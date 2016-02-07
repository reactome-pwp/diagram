package org.reactome.web.diagram.data.interactors.model;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.util.chemical.ChEBI_ImageLoader;
import org.reactome.web.diagram.util.pdbe.PDBeLoader;
import org.reactome.web.diagram.util.pdbe.model.PDBObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorEntity extends DiagramInteractor implements Draggable, PDBeLoader.Handler, ChEBI_ImageLoader.Handler {

    private String accession;
    private String alias;
    private boolean chemical;

    private PDBObject pdbObject;
    private ImageElement image;

    private Set<InteractorLink> links = new HashSet<>();

    public InteractorEntity(String accession, String alias) {
        this.accession = accession;
        this.alias = alias;
        this.chemical = accession.toLowerCase().contains("chebi");
    }

    public InteractorLink addInteraction(Node node, String id, double score) {
        InteractorLink link = new DynamicLink(node, this, id, score);
        links.add(link);
        return link;
    }

    public boolean removeLink(DynamicLink link){
        return links.remove(link);
    }

    @Override
    public String getAccession() {
        return accession;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isChemical() {
        return chemical;
    }

    public Coordinate getCentre() {
        return CoordinateFactory.get(minX + (maxX - minX) / 2.0, minY + (maxY - minY) / 2.0);
    }

    public String getDisplayName() {
        return alias != null ? alias : accession;
    }

    public String getDetails() {
        if (pdbObject != null) {
            return  "PDBe: " + pdbObject.getPdbid() + "    " + "Chain: " + pdbObject.getChain() +
                    "\n" + "Resolution: " + pdbObject.getResolution() +
                    "\n" + "Coverage: " + pdbObject.getCoverage() +
                    "\n" + "PDBe Range: " + pdbObject.getPdbRange() +
                    "\n" + "UniProt Range: " + pdbObject.getUniprotRange();
        }
        return null;
    }

    public ImageElement getImage() {
        if (image == null) setImageURL();
        return image;
    }

    public boolean isLaidOut() {
        return minX != null && maxX != null && minY != null && maxY != null;
    }

    @Override
    public boolean isHovered(Coordinate pos) {
        return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY;
    }

    @Override
    public boolean isVisible() {
        for (InteractorLink link : links) {
            if (link.isVisible()) return true;
        }
        return false;
    }

    public Collection<InteractorLink> getLinks() {
        return links;
    }

//    public PDBObject getPdbObject() {
//        return pdbObject;
//    }

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
    public void drag(double deltaX, double deltaY) {
        minX += deltaX;
        maxX += deltaX;
        minY += deltaY;
        maxY += deltaY;
        for (InteractorLink interactorLink : links) {
            interactorLink.setBoundaries(getCentre());
        }
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

    private void setImageURL() {
        if (chemical) {
            image = ImageElement.as(ChEBI_ImageLoader.LOADING.getElement());
            ChEBI_ImageLoader.get().loadImage(this, accession);
        } else {
            image = ImageElement.as(PDBeLoader.LOADING.getElement());
            PDBeLoader.get().loadBestStructure(this, accession);
        }
    }

    @Override
    public void onChemicalImageLoaded(Image image) {
        this.image = ImageElement.as(image.getElement());
    }

    @Override
    public void onPDBObjectLoaded(PDBObject pdbObject) {
        this.pdbObject = pdbObject;
    }

    @Override
    public void onImageLoaded(Image image) {
        this.image = ImageElement.as(image.getElement());
    }
}
