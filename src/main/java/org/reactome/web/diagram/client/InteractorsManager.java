package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.InteractorsStatus;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.EntityDecoratorSelectedEvent;
import org.reactome.web.diagram.events.InteractorsRequestCanceledEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsManager implements DiagramRequestedHandler, DiagramLoadedHandler,
        EntityDecoratorSelectedHandler {
    private static InteractorsManager interactorsManager;

    private EventBus eventBus;
    private InteractorsStatus status;

    public static void initialise(EventBus eventBus) {
        if (interactorsManager != null) {
            throw new RuntimeException("Interactors Manager has already been initialised. " +
                    "Only one initialisation is permitted.");
        }
        interactorsManager = new InteractorsManager(eventBus);
    }

    public static InteractorsManager get() {
        if (interactorsManager == null) {
            throw new RuntimeException("Interactors Manager has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return interactorsManager;
    }

    InteractorsManager(EventBus eventBus) {
        this.eventBus = eventBus;
        addHandlers();
    }

    public void addHandlers() {
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
    }

    public void close() {
        eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.status = event.getContext().getInteractorsStatus();
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.status = null;
    }

    @Override
    public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
        GraphObject graphObject = event.getGraphObject();
        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            if (pe.getIdentifier() != null) {
                status.onBurstToggle(event.getSummaryItem(), pe.getIdentifier());
                if (!status.isVisible()) this.close();
            }
        }
    }
}
