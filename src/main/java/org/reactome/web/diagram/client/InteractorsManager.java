package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.LinkCommon;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.DynamicLink;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.InteractorsCollapsedHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsManager implements DiagramLoadedHandler, DiagramRequestedHandler,
        InteractorsCollapsedHandler, InteractorsResourceChangedHandler {

    private EventBus eventBus;

    private DiagramContext context;
    private String currentResource;

    public InteractorsManager(EventBus eventBus) {
        this.eventBus = eventBus;
        addHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void addHandlers() {
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsCollapsedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    public String getCurrentResource() {
        return currentResource;
    }

    //Why do we need a layout node? easy... layout! layout! layout! :D
    public void loadInteractors(Node node) {
        InteractorsLayout layoutBuilder = new InteractorsLayout(node);
        GraphPhysicalEntity p = node.getGraphObject();
        Set<RawInteractor> rawInteractors = context.getInteractors().getRawInteractors(currentResource, p.getIdentifier());
        if (rawInteractors != null) {
            int n = rawInteractors.size();
            int i = 0;
            for (RawInteractor rawInteractor : rawInteractors) {

                //TODO: Cover the case when an entity in the diagram interacts with another one in the diagram (Static link)

                InteractorEntity interactor = getOrCreateInteractorEntity(rawInteractor.getAcc());

                layoutBuilder.doLayout(interactor, i++, n);

                Set<InteractorLink> links = new HashSet<>();
                context.getInteractors().cache(currentResource, node, interactor);
                for (InteractorLink link : interactor.addInteraction(node, rawInteractor.getId(), rawInteractor.getScore())) {
                    context.getInteractors().cache(currentResource, node, link);
                    links.add(link);
                }

                //next block (adding to the QuadTree) also needs to be done after the doLayout
                context.getInteractors().addInteractor(currentResource, interactor);
                for (InteractorLink link : links) {
                    context.getInteractors().addInteractor(currentResource, link);
                }
            }
        }
        eventBus.fireEventFromSource(new InteractorsLayoutUpdatedEvent(), this);
    }

    public void createNewLinksForExistingInteractors(Node node, Collection<DiagramInteractor> interactors) {
        //TODO: Cover the case when an entity in the diagram interacts with another one in the diagram (Static link)

        List<InteractorEntity> entities = new LinkedList<>();
        for (DiagramInteractor interactor : interactors) {
            if (interactor instanceof InteractorEntity) {
                entities.add((InteractorEntity) interactor);
            }
        }

        int n = entities.size(), i = 0;
        for (InteractorEntity entity : entities) {
            recalculateLayoutIfNeeded(node, entity, i++, n);
            for (LinkCommon linkCommon : entity.getUniqueLinks()) {
                for (InteractorLink link : entity.addInteraction(node, linkCommon.getId(), linkCommon.getScore())) {
                    context.getInteractors().cache(currentResource, node, link);
                    context.getInteractors().addInteractor(currentResource, link);
                }
            }
        }
    }

    public void recalculateLayoutIfNeededAndSetVisibility(Node node, Collection<InteractorLink> links, boolean visible) {
        int n = links.size(), i = 0;
        for (InteractorLink link : links) {
            if (link instanceof DynamicLink) {
                InteractorEntity entity = ((DynamicLink) link).getInteractorEntity();
                recalculateLayoutIfNeeded(node, entity, i++, n);
            }
        }
        for (InteractorLink link : links) {
            link.setVisible(visible);
        }
    }

    private void recalculateLayoutIfNeeded(Node node, InteractorEntity entity, int i, int n) {
        if (InteractorsLayout.doLayout(node, entity, i, n, !entity.isVisible())) {
            context.getInteractors().updateInteractor(currentResource, entity);
            for (InteractorLink link : entity.getLinks()) {
                //When the entity has been moved, all the links boundaries need to be updated
                link.setBoundaries(entity.getCentre());
                context.getInteractors().updateInteractor(currentResource, link);
            }
        }
    }

    private InteractorEntity getOrCreateInteractorEntity(String acc) {
        InteractorEntity interactor = context.getInteractors().getDiagramInteractor(currentResource, acc);
        if (interactor == null) {
            interactor = new InteractorEntity(acc);
            context.getInteractors().cache(currentResource, interactor);
        }
        return interactor;
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        context = null;
    }

    @Override
    public void onInteractorsCollapsed(InteractorsCollapsedEvent event) {
        for (InteractorLink link : context.getInteractors().getDiagramInteractions(currentResource)) {
            link.setVisible(false);
        }
        eventBus.fireEventFromSource(new InteractorsLayoutUpdatedEvent(), this);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
    }
}
