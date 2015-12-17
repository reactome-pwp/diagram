package org.reactome.web.diagram.data.interactors.raw;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface DiagramInteractors {

    /**
     * It is the PSICQUIC resource (e.g. IntAct, MINT, etc)
     */
    String getResource();

    /**
     * It depends on the accession type for the resource
     * (e.g. for UniProt http://www.uniprot.org/uniprot/##ID##)
     */
    String getInteractorUrl();

    /**
     * It depends on the resource
     * (e.g. for IntAct http://www.ebi.ac.uk/intact/interaction/##ID##)
     */
    String getInteractionUrl();

    /**
     * The list of entities with their interactor for the diagram
     */
    List<EntityInteractor> getEntities();

    /**
     * The list of synonyms for the interactors (to avoid data duplication)
     */
    List<Synonym> getSynonyms();

}
