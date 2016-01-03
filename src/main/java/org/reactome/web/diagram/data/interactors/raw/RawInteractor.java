package org.reactome.web.diagram.data.interactors.raw;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface RawInteractor {

    /**
     * The accession (identifier) for the interactor (e.g. UniProt, ChEBI, etc.)
     */
    String getAcc();

    /**
     * Interaction identifier (e.g. IntAct ID)
     */
    String getId();

    /**
     * The score for the current interaction
     */
    double getScore();

}
