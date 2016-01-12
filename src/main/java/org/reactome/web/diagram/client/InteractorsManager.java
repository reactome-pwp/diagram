package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.interactors.common.LinkCommon;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.DynamicLink;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.loader.InteractorsDetailsLoader;
import org.reactome.web.diagram.data.loader.InteractorsLoader;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsManager implements InteractorsLoader.Handler,
        DiagramLoadedHandler, DiagramRequestedHandler,
        InteractorsCollapsedHandler, InteractorsRequestCanceledHandler, InteractorsResourceChangedHandler {

    private EventBus eventBus;

    private InteractorsDetailsLoader interactorsLoader;
    private DiagramContext context;
    private String currentResource;

    private InteractorsLayout layoutBuilder;

    public InteractorsManager(EventBus eventBus) {
        this.eventBus = eventBus;
        this.interactorsLoader = new InteractorsDetailsLoader(this);
        addHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void addHandlers() {
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsCollapsedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsRequestCanceledEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    public String getCurrentResource() {
        return currentResource;
    }

    //Why do we need a layout node? easy... layout! layout! layout! :D
    public void loadInteractors(Node node) {
        layoutBuilder = new InteractorsLayout(node);
        interactorsLoader.load(context.getContent().getStableId(), currentResource);
    }

    @Override
    public void interactorsLoaded(RawInteractors rawInteractors, long time) {
        if (context == null || layoutBuilder == null) return;
        for (RawInteractorEntity rawInteractorEntity : rawInteractors.getEntities()) {
            String fromAcc = rawInteractorEntity.getAcc();
            if (!layoutBuilder.getAcc().equals(fromAcc)) continue;

            int n = rawInteractorEntity.getInteractors().size();
            int i = 0;
            for (RawInteractor rawInteractor : rawInteractorEntity.getInteractors()) {

                //TODO: Cover the case when an entity in the diagram interacts with another one in the diagram (Static link)

                InteractorEntity interactor = getOrCreateInteractorEntity(rawInteractor.getAcc());

                layoutBuilder.doLayout(interactor, i++, n);

                Set<InteractorLink> links = new HashSet<>();

                Node node = layoutBuilder.getNode();
                context.getContent().cache(currentResource, node, interactor);
                for (InteractorLink link : interactor.addInteraction(node, rawInteractor.getId(), rawInteractor.getScore())) {
                    context.getContent().cache(currentResource, node, link);
                    links.add(link);
                }

                //next block (adding to the QuadTree) also needs to be done after the doLayout
                context.addInteractor(currentResource, interactor);
                for (InteractorLink link : links) {
                    context.addInteractor(currentResource, link);
                }
            }
        }
        layoutBuilder = null;
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
                    context.getContent().cache(currentResource, node, link);
                    context.addInteractor(currentResource, link);
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
            context.updateInteractor(currentResource, entity);
            for (InteractorLink link : entity.getLinks()) {
                //When the entity has been moved, all the links boundaries need to be updated
                link.setBoundaries(entity.getCentre());
                context.updateInteractor(currentResource, link);
            }
        }
    }

    private InteractorEntity getOrCreateInteractorEntity(String acc) {
        InteractorEntity interactor = context.getContent().getDiagramInteractor(currentResource, acc);
        if (interactor == null) {
            interactor = new InteractorEntity(acc);
            context.getContent().cache(currentResource, interactor);
        }
        return interactor;
    }

    @Override
    public void onInteractorsLoaderError(Throwable exception) {
        String msg = "There was a problem loading the interactors for " + layoutBuilder.getNode().getDisplayName();
        eventBus.fireEventFromSource(new DiagramInternalErrorEvent(msg), this);
        layoutBuilder = null;
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
        for (InteractorLink link : context.getContent().getDiagramInteractions(currentResource)) {
            link.setVisible(false);
        }
        eventBus.fireEventFromSource(new InteractorsLayoutUpdatedEvent(), this);
    }

    @Override
    public void onInteractorsRequestCanceled(InteractorsRequestCanceledEvent event) {
        interactorsLoader.cancel();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
    }
}
