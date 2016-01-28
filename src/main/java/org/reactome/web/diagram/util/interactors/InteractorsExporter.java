package org.reactome.web.diagram.util.interactors;

import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.util.MapSet;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsExporter {

    public static void exportInteractors(String filename, MapSet<String, RawInteractor> interactors) {
        alertDownload(filename, getFileContent(interactors));
    }

    public static native boolean fileSaveScriptAvailable() /*-{
        return !!$wnd.saveAs;
    }-*/;

    /**
     * Downloads a file with the interactors
     *
     * @param text from preview
     */
    public static native void alertDownload(String filename, String text) /*-{
        $wnd.saveAs(
            new Blob(
                [text]
                , {type: "text/plain;charset=ISO-8859-8;"}
            )
            , [filename]
        );
    }-*/;

    private static String getFileContent(MapSet<String, RawInteractor> interactors) {
        StringBuilder builder = new StringBuilder();
        // Add Header
        builder.append("Interactor A").append("\t")
                .append("Interactor B").append("\t")
                .append("InteractionID").append("\t")
                .append("miScore").append("\t\n");
        for (String key : interactors.keySet()) {
            Set<RawInteractor> rawInteractors = interactors.getElements(key);
            if(rawInteractors != null) {
                for (RawInteractor rawInteractor : rawInteractors) {
                    builder.append(key).append("\t")
                            .append(rawInteractor.getAcc()).append("\t")
                            .append(rawInteractor.getId()).append("\t")
                            .append(rawInteractor.getScore()).append("\t\n");
                }
            }
        }
        return builder.toString();
    }
}
