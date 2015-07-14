package org.reactome.web.diagram.data.layout;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface NodeAttachment {

    String getLabel();

    String getDescription();

    @Deprecated
    Long getTrackId();

    Long getReactomeId();

    @Deprecated
    String getRenderableClass();

    Shape getShape();

}
