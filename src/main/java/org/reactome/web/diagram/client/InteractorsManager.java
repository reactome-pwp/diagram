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
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.InteractorsCollapsedHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;
import org.reactome.web.diagram.renderers.interactor.InteractorRenderer;
import org.reactome.web.diagram.renderers.interactor.InteractorRendererManager;
import org.reactome.web.diagram.util.interactors.InteractorsLayout;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsManager implements DiagramLoadedHandler, DiagramRequestedHandler,
        InteractorsCollapsedHandler, InteractorsResourceChangedHandler {

    private static final int MAX_INTERACTORS = 10;

    private EventBus eventBus;

    private DiagramContext context;
    private String currentResource;

    private DiagramInteractor hovered;

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

    public DiagramInteractor getHovered() {
        return hovered;
    }

    /**
     * In every zoom step the way the elements are drawn (even if they are drawn or not) is defined by the
     * renderer assigned. The most accurate and reliable way of finding out the hovered object by the mouse
     * pointer is using the renderer isHovered method.
     */
    public Collection<DiagramInteractor> getHovered(Coordinate model){
        List<DiagramInteractor> rtn = new LinkedList<>();
        if(context==null) return rtn;
        Collection<DiagramInteractor> target = context.getInteractors().getHoveredTarget(currentResource, model,context.getDiagramStatus().getFactor());
        for (DiagramInteractor interactor : target) {
            InteractorRenderer renderer = InteractorRendererManager.get().getRenderer(interactor);
            if(renderer.isVisible(interactor) && interactor.isHovered(model)){
                rtn.add(interactor);
            }
        }
        return rtn;
    }

    private int getNumberOfInteractorsToDraw(Collection items){
        if(items==null) return 0;
        int n = items.size();
        return n <= MAX_INTERACTORS ? n : MAX_INTERACTORS;
    }

    public boolean isHighlighted(DiagramInteractor item) {
        return Objects.equals(hovered, item);
    }

    public boolean isHoveredVisible(){
        return hovered != null && InteractorRendererManager.get().getRenderer(hovered).isVisible(hovered);
    }

    //Why do we need a layout node? easy... layout! layout! layout! :D
    public void loadInteractors(Node node) {
        InteractorsLayout layoutBuilder = new InteractorsLayout(node);
        GraphPhysicalEntity p = node.getGraphObject();
        Set<RawInteractor> rawInteractors = context.getInteractors().getRawInteractors(currentResource, p.getIdentifier());
        if (rawInteractors != null) {
            int n = getNumberOfInteractorsToDraw(rawInteractors);
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
                if(i == MAX_INTERACTORS) break; //No more than MAX_INTERACTORS elements can be visible
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

        int i = 0;
        int n = getNumberOfInteractorsToDraw(entities);
        for (InteractorEntity entity : entities) {
            recalculateLayoutIfNeeded(node, entity, i++, n);
            for (LinkCommon linkCommon : entity.getUniqueLinks()) {
                for (InteractorLink link : entity.addInteraction(node, linkCommon.getId(), linkCommon.getScore())) {
                    context.getInteractors().cache(currentResource, node, link);
                    context.getInteractors().addInteractor(currentResource, link);
                }
            }
            if(i == MAX_INTERACTORS) break; //No more than MAX_INTERACTORS elements can be visible
        }
    }

    public void recalculateLayoutIfNeededAndSetVisibility(Node node, Collection<InteractorLink> links, boolean visible) {
        int i = 0;
        int n = getNumberOfInteractorsToDraw(links);
        for (InteractorLink link : links) {
            if (link instanceof DynamicLink) {
                InteractorEntity entity = ((DynamicLink) link).getInteractorEntity();
                recalculateLayoutIfNeeded(node, entity, i++, n);
            }
            if(i == MAX_INTERACTORS) break; //No more than MAX_INTERACTORS elements can be visible
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

    public InteractorHoveredEvent setHovered(DiagramInteractor hovered) {
        if(!Objects.equals(this.hovered, hovered)) {
            this.hovered = hovered;
            return new InteractorHoveredEvent(hovered);
        }
        return null;
    }

    public void updateInteractor(InteractorEntity entity){
        context.getInteractors().updateInteractor(currentResource, entity);
    }
}
