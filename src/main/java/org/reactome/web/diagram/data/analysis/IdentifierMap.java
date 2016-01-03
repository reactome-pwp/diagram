package org.reactome.web.diagram.data.analysis;

import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface IdentifierMap {

    String getResource();

    Set<String> getIds();
}
