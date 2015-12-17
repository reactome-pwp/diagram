package org.reactome.web.diagram.data.interactors.raw;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface EntityInteractor {

    /**
     * Diagram entity identifier (UniProt, ChEBI)
     */
    String getAcc();

    /**
     * List of interactors for a given resource (specified in DiagramInteractors interface)
     */
    List<Interactor> getInteractors();
}
