package org.reactome.web.diagram.client;

import org.reactome.web.diagram.common.DisplayManager;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.*;
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

    public void fitDiagram(DiagramContent c, boolean animation) {
        displayManager.display(c.getMinX(), c.getMinY(), c.getMaxX(), c.getMaxY(), animation);
    }

    public Set<DiagramObject> getRelatedDiagramObjects(GraphObject item) {
        Set<DiagramObject> toDisplay = new HashSet<>();
        if (item instanceof GraphReactionLikeEvent) {
            toDisplay.addAll(getElementsToDisplay((GraphReactionLikeEvent) item));
        } else if (item instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) item;
            for (GraphReactionLikeEvent rle : pe.participatesIn()) {
                toDisplay.addAll(getElementsToDisplay(rle));
            }
        } else if (item instanceof GraphPathway) {
            toDisplay.addAll(item.getDiagramObjects());
        } else if (item instanceof GraphSubpathway) {
            GraphSubpathway subpathway = (GraphSubpathway) item;
            //DO NOT CALL subpathway.getDiagramObjects here because we need also the participants :)
            for (GraphObject obj : subpathway.getContainedEvents()) {
                if(obj instanceof GraphReactionLikeEvent) {
                    GraphReactionLikeEvent rle = (GraphReactionLikeEvent) obj;
                    toDisplay.addAll(getElementsToDisplay(rle));
                }
            }
        }
        return toDisplay;
    }

    public void displayDiagramObjects(GraphObject item) {
        Set<DiagramObject> toDisplay = getRelatedDiagramObjects(item);
        this.displayManager.display(toDisplay, true);
    }

    private Collection<DiagramObject> getElementsToDisplay(GraphReactionLikeEvent rle) {
        Set<DiagramObject> toDisplay = new HashSet<>(rle.getDiagramObjects());
        Set<Long> target = new HashSet<>();
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

    private Collection<DiagramObject> getDiagramObjectsParticipatingInReaction(Collection<GraphPhysicalEntity> entities,
                                                                               Set<Long> target) {
        Set<DiagramObject> rtn = new HashSet<>();
        for (GraphPhysicalEntity entity : entities) {
            for (DiagramObject object : entity.getDiagramObjects()) {
                if (object instanceof Node) {
                    Node node = (Node) object;
                    for (Connector connector : node.getConnectors()) {
                        if (target.contains(connector.getEdgeId())) {
                            rtn.add(node);
                        }
                    }
                }
            }
        }
        return rtn;
    }
}
