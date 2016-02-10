package org.reactome.web.diagram.util.interactors;

import org.reactome.web.diagram.client.DiagramFactory;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResourceNameFormatter {

    /**
     * Checks whether the resource is 'static' and applies the proper name for it.
     * In any other case it changes the name by capitalizing the first character
     * only in case all letters are lowercase
     */
    public static String format(String originalName) {
        if (originalName.equals(DiagramFactory.INTERACTORS_INITIAL_RESOURCE)){
            return DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME; // returns the proper static name, e.g. IntAct (Static)
        }

        String output;
        if (originalName.equals(originalName.toLowerCase())) {
            //Capitalize the first character of the resource
            output = originalName.substring(0, 1).toUpperCase() + originalName.substring(1);
        } else {
            output = originalName;
        }
        return output;
    }
}
