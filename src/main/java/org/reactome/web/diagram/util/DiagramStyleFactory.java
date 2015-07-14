package org.reactome.web.diagram.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DiagramStyleFactory {

    private static DiagramMainResources MAIN_RESOURCES = null;

    /**
     * A ClientBundle of resources used by this module.
     */
    public interface DiagramMainResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(FireworksStyle.DEFAULT_CSS)
        public FireworksStyle analysisStyle();

    }

    /**
     * Styles used by this module.
     */
    @CssResource.ImportedWithPrefix("reactome-diagram")
    public interface FireworksStyle extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String DEFAULT_CSS = "org/reactome/web/diagram/style/DiagramViewer.css";

        String popup();
        String popupTopLeft();
        String popupTopRight();
        String popupBottomLeft();
        String popupBottomRight();
    }

    public static FireworksStyle getAnalysisStyle(){
        if(MAIN_RESOURCES ==null){
            MAIN_RESOURCES = GWT.create(DiagramMainResources.class);
            MAIN_RESOURCES.analysisStyle().ensureInjected();
        }
        return MAIN_RESOURCES.analysisStyle();
    }


}