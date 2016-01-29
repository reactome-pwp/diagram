package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.LinkCommon;
import org.reactome.web.diagram.data.interactors.model.*;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.InteractorsCollapsedHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;
import org.reactome.web.diagram.renderers.interactor.InteractorRenderer;
import org.reactome.web.diagram.renderers.interactor.InteractorRendererManager;
import org.reactome.web.diagram.util.MapSet;
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
        if (items == null) return 0;
        int n = items.size();
        return n <= MAX_INTERACTORS ? n : MAX_INTERACTORS;
    }

    public boolean isHighlighted(DiagramInteractor item) {
        return Objects.equals(hovered, item);
    }

    //Why do we need a layout node? easy... layout! layout! layout! :D
    public void loadInteractors(Node node) {
        InteractorsLayout layoutBuilder = new InteractorsLayout(node);
        GraphPhysicalEntity p = node.getGraphObject();
        InteractorsContent interactors = context.getInteractors();
        List<RawInteractor> rawInteractors = interactors.getRawInteractors(currentResource, p.getIdentifier());

        //Keeping a list of the dynamic interactors will help later to decide the number of visible interactors
        List<RawInteractor> dynamicInteractors = new LinkedList<>();
        MapSet<String, GraphObject> map = context.getContent().getIdentifierMap();
        for (RawInteractor rawInteractor : rawInteractors) {
            String acc = rawInteractor.getAcc();
            //The following line removes resource name prefixes in the accession because the graph do not have them (CHEBI:12345 -> 12345)
            Set<GraphObject> objects = map.getElements(acc.replaceAll("^\\w+[-:_]", ""));
            if (objects != null) {
                //All the static links can be created since they do not clutter the view
                for (GraphObject object : objects) {
                    for (DiagramObject nodeTo : object.getDiagramObjects()) {
                        InteractorLink link;
                        if (node.equals(nodeTo)) {
                            link = new LoopLink(node, rawInteractor.getId(), rawInteractor.getScore());
                        } else {
                            link = new StaticLink(node, (Node) nodeTo, rawInteractor.getId(), rawInteractor.getScore());
                        }
                        interactors.cache(currentResource, node, link);
                        interactors.addInteractor(currentResource, link);
                    }
                }
            } else {
                dynamicInteractors.add(rawInteractor);
            }
        }

        //From those that are not visible, we pick the top "allowed" number
        int n = getNumberOfInteractorsToDraw(dynamicInteractors);
        for (int i = 0; i < n; i++) {  //please note that "n" can be increased if the interactors are present in the diagram
            RawInteractor rawInteractor = rawInteractors.get(i);

            String acc = rawInteractor.getAcc();
            //In this case the interactor is NOT present in the diagram so we have to create an interactor with its link to the node
            InteractorEntity interactor = getOrCreateInteractorEntity(acc);

            layoutBuilder.doLayout(interactor, i, n);  //the maximum number of elements is used here for layout beauty purposes

            Set<InteractorLink> links = new HashSet<>();
            interactors.cache(currentResource, node, interactor);
            for (InteractorLink link : interactor.addInteraction(node, rawInteractor.getId(), rawInteractor.getScore())) {
                interactors.cache(currentResource, node, link);
                links.add(link);
            }

            //next block (adding to the QuadTree) also needs to be done after the doLayout
            interactors.addInteractor(currentResource, interactor);
            for (InteractorLink link : links) {
                interactors.addInteractor(currentResource, link);
            }
        }
        eventBus.fireEventFromSource(new InteractorsLayoutUpdatedEvent(), this);
    }

    public void createNewLinksForExistingInteractors(final Node node, Collection<DiagramInteractor> interactors) {
        List<InteractorEntity> entities = new LinkedList<>();
        for (DiagramInteractor interactor : interactors) {
            if (interactor instanceof InteractorEntity) {
                entities.add((InteractorEntity) interactor);
            } else if (interactor instanceof StaticLink) {
                StaticLink aux = (StaticLink) interactor;
                //Here both (from and to) need to be checked as target for the static interactor
                for (Node to : Arrays.asList(aux.getNodeFrom(), aux.getNodeTo())) {
                    if(!to.equals(node)){
                        StaticLink link = new StaticLink(node, to, aux.getId(), aux.getScore());
                        context.getInteractors().cache(currentResource, node, link);
                        context.getInteractors().addInteractor(currentResource, link);
                    }
                }
            } else if (interactor instanceof LoopLink) {
                LoopLink aux = (LoopLink) interactor;
                LoopLink link = new LoopLink(node, aux.getId(), aux.getScore());
                context.getInteractors().cache(currentResource, node, link);
                context.getInteractors().addInteractor(currentResource, link);
            }
        }

        int n = getNumberOfInteractorsToDraw(entities);
        for (int i = 0; i < getNumberOfInteractorsToDraw(entities); i++) {
            InteractorEntity entity = entities.get(i);
            recalculateLayoutIfNeeded(node, entity, i, n);
            for (LinkCommon linkCommon : entity.getUniqueLinks()) {
                for (InteractorLink link : entity.addInteraction(node, linkCommon.getId(), linkCommon.getScore())) {
                    context.getInteractors().cache(currentResource, node, link);
                    context.getInteractors().addInteractor(currentResource, link);
                }
            }
        }
    }

    public void recalculateLayoutIfNeededAndSetVisibility(Node node, List<InteractorLink> links, boolean visible) {
        int n = getNumberOfInteractorsToDraw(links);
        for (int i = 0; i < n; i++) {
            InteractorLink link = links.get(i);
            if (link instanceof DynamicLink) {
                InteractorEntity entity = ((DynamicLink) link).getInteractorEntity();
                recalculateLayoutIfNeeded(node, entity, i, n);
            }
        }
        //The visibility of the links has to be changed AFTER recalculating the layout
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
        InteractorsContent interactors = context.getInteractors();
        interactors.updateInteractor(currentResource, entity);
        for (InteractorLink link : entity.getLinks()) {
            interactors.updateInteractor(currentResource, link);
        }
    }
}
