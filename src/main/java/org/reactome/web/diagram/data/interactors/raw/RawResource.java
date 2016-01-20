package org.reactome.web.diagram.data.interactors.raw;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface RawResource {

    String getName();

    String getSoapURL();

    String getRestURL();

    Boolean getActive();
}
