package org.reactome.web.diagram.client;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DiagramFactory {
    public static boolean CONSOLE_VERBOSE = false;
    public static boolean EVENT_BUS_VERBOSE = false;
    public static boolean SHOW_INFO = false;

    //Added for testing
    public static DiagramViewer createDiagramViewer() {
        return  new DiagramViewerImpl();
    }
}
