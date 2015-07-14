package org.reactome.web.diagram.data.loader;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.DiagramContentFactory;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.events.GraphLoadedEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.util.Console;

/**
 * Implements a two step loading strategy
 *  1st step: Load Diagram
 *  2nd step: Load Graph
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LoaderManager implements LayoutLoader.Handler, GraphLoader.Handler {

    private EventBus eventBus;

    private LayoutLoader layoutLoader;
    private GraphLoader graphLoader;
    private DiagramContent content;

    public LoaderManager(EventBus eventBus) {
        this.eventBus = eventBus;
        this.layoutLoader = new LayoutLoader(this);
        this.graphLoader = new GraphLoader(this);
    }

    public void cancel(){
        this.layoutLoader.cancel();
        this.graphLoader.cancel();
        this.content = null;
    }

    public void load(String stId){
        this.cancel(); //First cancel possible loading process
        this.layoutLoader.load(stId);
    }

    @Override
    public void layoutLoaded(Diagram diagram, long time) {
        //This is querying the server so the following code is executed straight forward
        long start = System.currentTimeMillis();
        this.graphLoader.load(diagram.getStableId());
        this.content =  DiagramContentFactory.getDiagramContent(diagram);
        time += System.currentTimeMillis() - start;
        this.eventBus.fireEventFromSource(new LayoutLoadedEvent(new DiagramContext(content), time), this);
    }

    @Override
    public void onLayoutLoaderError(Throwable exception) {
        //TODO
        Console.error("TODO: Treating exception " +  exception.getMessage(), this);
    }

    @Override
    public void graphLoaded(Graph graph, long time) {
        long start = System.currentTimeMillis();
        DiagramContentFactory.fillGraphContent(this.content, graph);
        time += System.currentTimeMillis() - start;
        this.eventBus.fireEventFromSource(new GraphLoadedEvent(content, time), this);
    }

    @Override
    public void onGraphLoaderError(Throwable exception) {
        //TODO
        Console.error("TODO: Treating exception " +  exception.getMessage(), this);
    }
}


