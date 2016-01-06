package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.InteractorsStatus;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsManager implements DiagramRequestedHandler, DiagramLoadedHandler,
        EntityDecoratorSelectedHandler,
        InteractorsLoadedHandler, InteractorsErrorHandler, InteractorsResourceChangedHandler, InteractorsFilteredHandler {
    private static InteractorsManager interactorsManager;

    private EventBus eventBus;
    private InteractorsStatus status;
    private String currentResource;

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
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
    }

    public void close() {
        status.clearBurstEntities();
        notifyStatusChanged();
        if(status!=null && !status.isVisible()) {
            eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        status = event.getContext().getInteractorsStatus();
        if(!status.isLoading()){
            if(!currentResource.equals(status.getResource())){
               eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(currentResource), this);
            }
        } else {
            notifyStatusChanged();
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        status = null;
        notifyStatusChanged();
    }

    @Override
    public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
        GraphObject graphObject = event.getGraphObject();
        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            if (pe.getIdentifier() != null) {
                status.onBurstToggle(event.getSummaryItem(), pe.getIdentifier());
                notifyStatusChanged();
            }
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        status.setLoading(false);
        status.setResource(event.getInteractors().getResource());
        status.setServerMsg(null);
        notifyStatusChanged();
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        status.setServerMsg(event.getMessage());
        status.setLoading(false);
        notifyStatusChanged();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
        status.setLoading(true);
        status.setServerMsg(null);
        notifyStatusChanged();
    }

    @Override
    public void onInteractorsFiltered(InteractorsFilteredEvent event) {
        status.setThreshold(event.getScore());
    }

    private void notifyStatusChanged(){
        eventBus.fireEventFromSource(new InteractorsStatusChangedEvent(status), this);
    }
}
