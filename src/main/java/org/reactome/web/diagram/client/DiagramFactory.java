package org.reactome.web.diagram.client;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DiagramFactory {

    public static boolean CONSOLE_VERBOSE = false;
    public static boolean EVENT_BUS_VERBOSE = false;

    public static String SERVER = "";
    public static String ILLUSTRATION_SERVER = SERVER;  //This can be set separately (e.g. reactomedev case)

    public static boolean SHOW_INFO = false;
    public static boolean SHOW_FIREWORKS_BTN = true;

    public static boolean WATERMARK = true;
    public static String WATERMARK_BASE_URL = "http://www.reactome.org/PathwayBrowser/";

    //Added for testing
    public static DiagramViewer createDiagramViewer() {
        return new DiagramViewerImpl();
    }
}
