package org.reactome.web.diagram.search;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface SearchResultObject {

    ImageResource getImageResource();

    String getPrimarySearchDisplay();

    String getSecondarySearchDisplay();

    void setSearchDisplay(String[] searchTerms);
}
