package org.reactome.web.diagram.renderers.interactor;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramZoomEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramZoomHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorRendererManager implements DiagramZoomHandler, DiagramLoadedHandler {

    private static InteractorRendererManager manager;

    private EventBus eventBus;

    private Map<String, InteractorRendererManager> s000 = new HashMap<>();
    private Map<String, InteractorRendererManager> s050 = new HashMap<>();
    private Map<String, InteractorRendererManager> s100 = new HashMap<>();
//    private Map<String, InteractorRendererManager> s200 = new HashMap<>();

    private InteractorRendererManager(EventBus eventBus) {
        this.eventBus = eventBus;
        initHandlers();
        initialiseRenderers();
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
    }

    public static void initialise(EventBus eventBus) {
        if (InteractorRendererManager.manager != null) {
            throw new RuntimeException("Interactor Renderer Manager has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        InteractorRendererManager.manager = new InteractorRendererManager(eventBus);
    }

    public static InteractorRendererManager get() {
        if (InteractorRendererManager.manager == null) {
            throw new RuntimeException("Interactor Renderer Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return InteractorRendererManager.manager;
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {

    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {

    }

    public void initialiseRenderers() {

    }
}
