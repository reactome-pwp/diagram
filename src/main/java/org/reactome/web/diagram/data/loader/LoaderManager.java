package org.reactome.web.diagram.data.loader;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.DiagramContentFactory;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.InteractorsRequestCanceledHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;

/**
 * Implements a three step loading strategy
 * 1st step: Loads Diagram
 * 2nd step: Loads Graph
 * 3rd step: Loads Interactors (if INTERACTORS_RESOURCE is not null)
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LoaderManager implements LayoutLoader.Handler, GraphLoader.Handler, InteractorsLoader.Handler,
        InteractorsResourceChangedHandler, InteractorsRequestCanceledHandler {

    //Every time the diagram widget is loaded will retrieve new data from the sever
    public static String version = "" + System.currentTimeMillis(); //UNIQUE per session

    //It has a value by default but it can be set to a different one so in every load
    //the "user preferred" interactors resource will be selected
    public static String INTERACTORS_RESOURCE = "Resource1"; // -> null here means DO NOT LOAD interactors

    private EventBus eventBus;

    private LayoutLoader layoutLoader;
    private GraphLoader graphLoader;
    private InteractorsLoader interactorsLoader;
    private DiagramContent content;

    public LoaderManager(EventBus eventBus) {
        this.eventBus = eventBus;
        layoutLoader = new LayoutLoader(this);
        graphLoader = new GraphLoader(this);
        interactorsLoader = new InteractorsLoader(this);

        //For the time being we only want to do something on demand for interactors
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsRequestCanceledEvent.TYPE, this);
    }

    public void cancel() {
        layoutLoader.cancel();
        graphLoader.cancel();
        interactorsLoader.cancel();
        content = null;
    }

    public void load(String stId) {
        cancel(); //First cancel possible loading process
        layoutLoader.load(stId);
    }

    @Override
    public void layoutLoaded(Diagram diagram, long time) {
        //This is querying the server so the following code is executed straight forward
        long start = System.currentTimeMillis();
        graphLoader.load(diagram.getStableId());
        content = DiagramContentFactory.getDiagramContent(diagram);
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new LayoutLoadedEvent(new DiagramContext(content), time), this);
    }

    @Override
    public void onLayoutLoaderError(Throwable exception) {
        eventBus.fireEventFromSource(new DiagramInternalErrorEvent("Diagram data " + exception.getMessage()), this);
    }

    @Override
    public void graphLoaded(Graph graph, long time) {
        long start = System.currentTimeMillis();
        if (INTERACTORS_RESOURCE != null)   //Checking here so no error message is displayed in this case
            interactorsLoader.load(graph.getStId(), INTERACTORS_RESOURCE);
        DiagramContentFactory.fillGraphContent(content, graph);
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new GraphLoadedEvent(content, time), this);
    }

    @Override
    public void onGraphLoaderError(Throwable exception) {
        eventBus.fireEventFromSource(new DiagramInternalErrorEvent("Graph content " + exception.getMessage()), this);
    }

    @Override
    public void interactorsLoaded(RawInteractors interactors, long time) {
        long start = System.currentTimeMillis();
        DiagramContentFactory.fillInteractorsContent(content, interactors);
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new InteractorsLoadedEvent(interactors, time), this);
    }

    @Override
    public void onInteractorsLoaderError(Throwable exception) {
        eventBus.fireEventFromSource(new InteractorsErrorEvent(INTERACTORS_RESOURCE + ": " + exception.getMessage()), this);
    }

    //The interactors loader is meant to be used not only when loading a new diagram but also on demand
    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        INTERACTORS_RESOURCE = event.getResource();
        interactorsLoader.load(content.getStableId(), INTERACTORS_RESOURCE);
    }

    @Override
    public void onInteractorsRequestCanceled(InteractorsRequestCanceledEvent event) {
        interactorsLoader.cancel();
    }
}