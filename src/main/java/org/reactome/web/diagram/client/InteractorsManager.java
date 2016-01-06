package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.InteractorsStatus;
import org.reactome.web.diagram.data.loader.LoaderManager;
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

    private DiagramContent content;

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
        this.currentResource = LoaderManager.INTERACTORS_RESOURCE;
        this.status = new InteractorsStatus(currentResource);
        addHandlers();
    }

    public void addHandlers() {
        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
        eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    public void close() {
        content.resetBurstInteractors();
        notifyStatusChanged();
        if (status != null && !status.isVisible()) {
            eventBus.fireEventFromSource(new InteractorsRequestCanceledEvent(), this);
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        content = event.getContext().getContent();
        status = new InteractorsStatus(LoaderManager.INTERACTORS_RESOURCE);
        status.setVisible(content.getNumberOfBustEntities(currentResource) > 0);
        if (!status.isLoading()) {
            if (!currentResource.equals(status.getResource())) {
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
        status.setVisible(content.getNumberOfBustEntities(currentResource) > 0);
        notifyStatusChanged();
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        currentResource = event.getInteractors().getResource();
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
        status.setResource(currentResource);
        status.setLoading(!content.isInteractorResourceCached(currentResource));
        status.setVisible(content.getNumberOfBustEntities(currentResource) > 0);
        status.setServerMsg(null);
        notifyStatusChanged();
    }

    @Override
    public void onInteractorsFiltered(InteractorsFilteredEvent event) {
        status.setThreshold(event.getScore());
    }

    private void notifyStatusChanged() {
        eventBus.fireEventFromSource(new InteractorsStatusChangedEvent(status), this);
    }
}
