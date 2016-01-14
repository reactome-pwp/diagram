package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.EntityDecoratorHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.renderers.common.HoveredItem;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LayoutManager implements DiagramLoadedHandler, DiagramRequestedHandler {

    private EventBus eventBus;

    private DiagramContext context;
    private HoveredItem hovered = null;
    private GraphObject selected = null;
    private Set<DiagramObject> halo = new HashSet<>();
    private Set<DiagramObject> flagged = new HashSet<>();

    public LayoutManager(EventBus eventBus) {
        this.eventBus = eventBus;
        addHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void addHandlers() {
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
    }

    public Set<DiagramObject> getFlagged() {
        return flagged;
    }

    public Set<DiagramObject> getHalo() {
        return halo;
    }

    public HoveredItem getHovered() {
        return hovered;
    }

    public DiagramObject getHoveredDiagramObject(){
        return hovered == null ? null : hovered.getDiagramObjects().get(0);
    }

    public GraphObject getSelected() {
        return selected;
    }

    public List<DiagramObject> getSelectedDiagramObjects() {
        return selected != null ? selected.getDiagramObjects() : new LinkedList<DiagramObject>();
    }

    public boolean isHighlighted(HoveredItem item){
        return Objects.equals(hovered, item);
    }

    public boolean isSelected(GraphObject graphObject){
        return Objects.equals(graphObject, this.selected);
    }

    public boolean resetHalo(){
        if (selected != null){
            halo.removeAll(selected.getDiagramObjects());
            return true;
        }
        return false;
    }

    public boolean resetHovered(){
        if (hovered != null) {
            hovered = null;
            return true;
        }
        return false;
    }

    public boolean resetFlagged(){
        if (this.flagged != null) {
            this.flagged = new HashSet<>();
            return true;
        }
        return false;
    }

    public boolean resetSelected(){
        if (this.selected != null) {
            this.selected = null;
            return true;
        }
        return false;
    }

    public void setFlagged(Set<DiagramObject> flagged) {
        this.flagged = flagged;
    }

    public GraphObjectHoveredEvent setHovered(HoveredItem hovered){
        DiagramObject item = null;
        if (hovered != null) {
            item = hovered.getHoveredObject();
            if (hovered.getAttachment() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getAttachment()), this);
            } else if (hovered.getSummaryItem() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getSummaryItem()), this);
            } else if (hovered.getContextMenuTrigger() != null) {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item, hovered.getContextMenuTrigger()), this);
            } else {
                this.eventBus.fireEventFromSource(new EntityDecoratorHoveredEvent(item), this);
            }
        }

        //Even though at the level of HoveredItem they are different, we only notify if the hovered diagram
        //object is actually different, so we do not take into account attachments or entities summary.
        DiagramObject prev = this.hovered != null ? this.hovered.getHoveredObject() : null;
        this.hovered = hovered;
        if (!Objects.equals(prev, item)) {
            GraphObject graphObject = item != null && item.getIsFadeOut() == null ? item.getGraphObject() : null;
            //we don't rely on the listener of the following event because finer grain of the hovering is lost
            return new GraphObjectHoveredEvent(graphObject, item);
        }
        return null;
    }

    public void setSelected(GraphObject graphObject) {
        if (graphObject == null) {
            this.halo = new HashSet<>();
            this.selected = null;
        } else {
            boolean fadeOut = !graphObject.getDiagramObjects().isEmpty();
            for (DiagramObject diagramObject : graphObject.getDiagramObjects()) {
                fadeOut &= diagramObject.getIsFadeOut() != null;
            }
            if (!fadeOut) {
                this.selected = graphObject;
                this.halo = graphObject.getRelatedDiagramObjects();
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (GraphPhysicalEntity parent : pe.getParentLocations()) {
                        this.halo.addAll(parent.getDiagramObjects());   //halo its parents but not the reactions where they participate
                    }
                }
            }
        }
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        context = null;
    }

}
