package org.reactome.web.diagram.renderers.layout;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramZoomEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramZoomHandler;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.layout.s000.*;
import org.reactome.web.diagram.renderers.layout.s050.*;
import org.reactome.web.diagram.renderers.layout.s100.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class RendererManager implements DiagramZoomHandler, DiagramLoadedHandler {

    private static RendererManager manager;

    private EventBus eventBus;

    private Map<String, Renderer> s000 = new HashMap<>();
    private Map<String, Renderer> s050 = new HashMap<>();
    private Map<String, Renderer> s100 = new HashMap<>();
//    private Map<String, Renderer> s200 = new HashMap<>();

    private Map<String, Renderer> current = s100;
    private ConnectorRenderer connectorRenderer = new ConnectorRenderer100();

    private RendererManager(EventBus eventBus) {
        this.eventBus = eventBus;
        initialiseRenderers();
        initHandlers();
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
    }

    public static void initialise(EventBus eventBus) {
        if (RendererManager.manager != null) {
            throw new RuntimeException("Renderer Manager has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        RendererManager.manager = new RendererManager(eventBus);
    }

    public static RendererManager get() {
        if (RendererManager.manager == null) {
            throw new RuntimeException("Renderer Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return RendererManager.manager;
    }

    public Renderer getRenderer(DiagramObject item) {
        if (item == null) return null;
        return this.current.get(item.getRenderableClass());
    }

    public Renderer getRenderer(String renderableClass) {
        if (renderableClass == null) return null;
        return this.current.get(renderableClass);
    }

    public Renderer getDiagramKeyRenderer(DiagramObject item){
        if (item == null) return null;
        return this.s100.get(item.getRenderableClass());
    }

    public ConnectorRenderer getConnectorRenderer() {
        return connectorRenderer;
    }

    public void initialiseRenderers() {
        Renderer aux;

        s000.put("Note", new NoteRenderer000());
        s000.put("Compartment", new CompartmentRenderer000());
        s000.put("Protein", new ProteinRenderer000());
        s000.put("Chemical", new ChemicalRenderer000());
        s000.put("Reaction", new ReactionRenderer000());
        s000.put("Complex", new ComplexRenderer000());
        s000.put("Entity", new OtherEntityRenderer000());
        s000.put("EntitySet", new SetRenderer000());
        s000.put("ProcessNode", new ProcessNodeRenderer000());
        s000.put("FlowLine", new FlowlineRenderer000());
        s000.put("Interaction", new InteractionRenderer000());
        s000.put("RNA", new RNARenderer000());
        s000.put("Gene", new GeneRenderer000());
        s000.put("Shadow", new ShadowRenderer000());
        aux = new LinkRenderer000();
        s000.put("EntitySetAndMemberLink", aux);
        s000.put("EntitySetAndEntitySetLink", aux);

        s050.put("Note", new NoteRenderer050());
        s050.put("Compartment", new CompartmentRenderer050());
        s050.put("Protein", new ProteinRenderer050());
        s050.put("Chemical", new ChemicalRenderer050());
        s050.put("Reaction", new ReactionRenderer050());
        s050.put("Complex", new ComplexRenderer050());
        s050.put("Entity", new OtherEntityRenderer050());
        s050.put("EntitySet", new SetRenderer050());
        s050.put("ProcessNode", new ProcessNodeRenderer050());
        s050.put("FlowLine", new FlowlineRenderer050());
        s050.put("Interaction", new InteractionRenderer050());
        s050.put("RNA", new RNARenderer050());
        s050.put("Gene", new GeneRenderer050());
        s050.put("Shadow", new ShadowRenderer050());
        aux = new LinkRenderer050();
        s050.put("EntitySetAndMemberLink", aux);
        s050.put("EntitySetAndEntitySetLink", aux);

        s100.put("Note", new NoteRenderer100());
        s100.put("Compartment", new CompartmentRenderer100());
        s100.put("Protein", new ProteinRenderer100());
        s100.put("Chemical", new ChemicalRenderer100());
        s100.put("Reaction", new ReactionRenderer100());
        s100.put("Complex", new ComplexRenderer100());
        s100.put("Entity", new OtherEntityRenderer100());
        s100.put("EntitySet", new SetRenderer100());
        s100.put("ProcessNode", new ProcessNodeRenderer100());
        s100.put("FlowLine", new FlowlineRenderer100());
        s100.put("Interaction", new InteractionRenderer100());
        s100.put("RNA", new RNARenderer100());
        s100.put("Gene", new GeneRenderer100());
        s100.put("Shadow", new ShadowRenderer100());
        aux = new LinkRenderer100();
        s100.put("EntitySetAndMemberLink", aux);
        s100.put("EntitySetAndEntitySetLink", aux);

//        s200.put("Note", new NoteRenderer200());
//        s200.put("Compartment", new CompartmentRenderer200());
//        s200.put("Protein", new ProteinRenderer200());
//        s200.put("Chemical", new ChemicalRenderer200());
//        s200.put("Reaction", new ReactionRenderer200());
//        s200.put("Complex", new ComplexRenderer200());
//        s200.put("Entity", new OtherEntityRenderer200());
//        s200.put("EntitySet", new SetRenderer200());
//        s200.put("ProcessNode", new ProcessNodeRenderer200());
//        s200.put("FlowLine", new FlowlineRenderer200());
//        s200.put("RNA", new RNARenderer200());
//        s200.put("Gene", new GeneRenderer200());
//        s200.put("Shadow", new ShadowRenderer200());
//        aux = new LinkRenderer200();
//        s200.put("EntitySetAndMemberLink", aux);
//        s200.put("EntitySetAndEntitySetLink", aux);

    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        this.setFactor(event.getFactor());
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.setFactor(event.getContext().getDiagramStatus().getFactor());
    }

    private void setFactor(double factor) {
        RendererProperties.setFactor(factor);
        if (factor < 0.5) {
            this.connectorRenderer = new ConnectorRenderer000();
            this.current = this.s000;
        } else if (factor < 1) {
            this.connectorRenderer = new ConnectorRenderer050();
            this.current = this.s050;
        } else if (factor < 2) {
            connectorRenderer = new ConnectorRenderer100();
            this.current = this.s100;
        } else {
            connectorRenderer = new ConnectorRenderer100();
            this.current = this.s100;
        }
    }
}
