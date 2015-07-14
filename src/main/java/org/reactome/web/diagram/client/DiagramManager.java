package org.reactome.web.diagram.client;

import org.reactome.web.diagram.common.DisplayManager;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.graph.model.PhysicalEntity;
import org.reactome.web.diagram.data.graph.model.ReactionLikeEvent;
import org.reactome.web.diagram.data.layout.Connector;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
class DiagramManager {

    private DisplayManager displayManager;

    DiagramManager(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    public void fitDiagram(DiagramContent c, boolean animation){
        displayManager.display(c.getMinX(), c.getMinY(), c.getMaxX(), c.getMaxY(), animation);
    }

    public Set<DiagramObject> getRelatedDiagramObjects(DatabaseObject item){
        Set<DiagramObject> toDisplay = new HashSet<DiagramObject>();
        if(item instanceof ReactionLikeEvent){
            toDisplay.addAll(getElementsToDisplay((ReactionLikeEvent) item));
        }else if(item instanceof PhysicalEntity) {
            PhysicalEntity pe = (PhysicalEntity) item;
            for (ReactionLikeEvent rle : pe.participatesIn()) {
                toDisplay.addAll(getElementsToDisplay(rle));
            }
        }
        return toDisplay;
    }

    public void displayDiagramObjects(DatabaseObject item){
        Set<DiagramObject> toDisplay = getRelatedDiagramObjects(item);
        this.displayManager.display(toDisplay, true);
    }

    private Collection<DiagramObject> getElementsToDisplay(ReactionLikeEvent rle){
        Set<DiagramObject> toDisplay = new HashSet<DiagramObject>(rle.getDiagramObjects());
        Set<Long> target = new HashSet<Long>();
        for (DiagramObject diagramObject : toDisplay) {
            target.add(diagramObject.getId());
        }
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getInputs(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getOutputs(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getCatalysts(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getActivators(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getInhibitors(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getRequirements(), target));
        return toDisplay;
    }

    private Collection<DiagramObject> getDiagramObjectsParticipatingInReaction(Collection<PhysicalEntity> entities,
                                                                               Set<Long> target){
        Set<DiagramObject> rtn = new HashSet<DiagramObject>();
        for (PhysicalEntity entity : entities) {
            for (DiagramObject object : entity.getDiagramObjects()) {
                if(object instanceof Node){
                    Node node = (Node) object;
                    for (Connector connector : node.getConnectors()) {
                        if(target.contains(connector.getEdgeId())){
                            rtn.add(node);
                        }
                    }
                }
            }
        }
        return rtn;
    }
}
