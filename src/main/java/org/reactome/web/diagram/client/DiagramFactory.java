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

    public static boolean RESPOND_TO_SEARCH_SHORTCUT = true; // Listen to ctrl (or cmd) + F and expand the search input

    //It has a value by default but it can be set to a different one so in every load
    //the "user preferred" interactors resource will be selected
    public static String INTERACTORS_INITIAL_RESOURCE = "static"; // -> null here means DO NOT LOAD interactors
    public static String INTERACTORS_INITIAL_RESOURCE_NAME = "IntAct/Static"; // --> it should be null if the one above is null

    //Added for testing
    public static DiagramViewer createDiagramViewer() {
        return new DiagramViewerImpl();
    }
}
