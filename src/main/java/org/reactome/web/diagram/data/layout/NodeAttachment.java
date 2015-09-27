package org.reactome.web.diagram.data.layout;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface NodeAttachment {

    String getLabel();

    String getDescription();

    Long getReactomeId();

    Shape getShape();

}
