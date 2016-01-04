package org.reactome.web.diagram.data;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.context.ContextDialogPanel;
import org.reactome.web.diagram.data.analysis.*;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.util.LruCache;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTree;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramContext {
    static final double ANALYSIS_MIN_PERCENTAGE = 0.03;

    //The number of elements for every QuadTree quadrant node
    static final int NUMBER_OF_ELEMENTS = 15;
    static final int INC_STEP = 10;

    static final int INTERACTORS_RESOURCE_CACHE_SIZE = 5;

    private DiagramContent content;
    private DiagramStatus diagramStatus;
    private QuadTree<DiagramObject> diagramObjects;
    private LruCache<String, QuadTree<DiagramInteractor>> interactors;
    private AnalysisStatus analysisStatus;

    private Map<GraphObject, ContextDialogPanel> dialogMap = new HashMap<>();

    public DiagramContext(DiagramContent content) {
        this.content = content;

        //It will create a QuadTree with the minimum elements per quadrant starting with
        //NUMBER_OF_ELEMENTS and increasing by steps of INC_STEP (Even though it could
        //penalise the loading time a little bit, this strategy improves the user experience
        //for pathways with few overlap and does its best for the others
        this.diagramObjects = createDiagramObjectTree(content.getDiagramObjects(), NUMBER_OF_ELEMENTS, INC_STEP);

        this.interactors = new LruCache<>(INTERACTORS_RESOURCE_CACHE_SIZE);

        //Status needs to be created every time we load a new content
        this.diagramStatus = new DiagramStatus();
    }

    //This method is not checking whether the interactors where previously put in place since
    //when it is called, the interactors have probably been retrieved "again" from the server
    //IMPORTANT: To avoid loading data that already exists -> CHECK BEFORE RETRIEVING :)
    public void addInteractors(String resource, Collection<DiagramInteractor> interactors){
        this.interactors.put(resource, createInteractorTree(interactors, NUMBER_OF_ELEMENTS, INC_STEP));
    }

    public void clearAnalysisOverlay() {
        this.analysisStatus = null;
        for (GraphObject graphObject : this.content.getDatabaseObjects()) {
            if (graphObject instanceof GraphPhysicalEntity) {
                ((GraphPhysicalEntity) graphObject).resetHit();
            } else if (graphObject instanceof GraphPathway) {
                ((GraphPathway) graphObject).resetHit();
            }
        }
    }

    public void setAnalysisOverlay(AnalysisStatus analysisStatus, PathwayIdentifiers pathwayIdentifiers, List<PathwaySummary> pathwaySummaries) {
        this.analysisStatus = analysisStatus;
        MapSet<String, GraphObject> map = this.content.getIdentifierMap();
        if (pathwayIdentifiers != null) {
            for (PathwayIdentifier identifier : pathwayIdentifiers.getIdentifiers()) {
                for (IdentifierMap identifierMap : identifier.getMapsTo()) {
                    for (String id : identifierMap.getIds()) {
                        Set<GraphObject> elements = map.getElements(id);
                        if (elements == null) continue;
                        for (GraphObject graphObject : elements) {
                            if (graphObject instanceof GraphPhysicalEntity) {
                                GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                                pe.setIsHit(identifier.getIdentifier(), identifier.getExp());
                            }
                        }
                    }
                }
            }
        }
        if (pathwaySummaries != null) {
            for (PathwaySummary pathwaySummary : pathwaySummaries) {
                EntityStatistics statistics = pathwaySummary.getEntities();
                if (statistics.getFound() > 0) {
                    //In this case process nodes DO NOT HAVE an identifier, but DO NOT use null here! use empty string
                    GraphPathway pathway = (GraphPathway) this.content.getDatabaseObject(pathwaySummary.getDbId());
                    Double percentage = statistics.getFound() / statistics.getTotal().doubleValue();
                    if (percentage < ANALYSIS_MIN_PERCENTAGE) percentage = ANALYSIS_MIN_PERCENTAGE;
                    pathway.setIsHit(percentage, pathwaySummary.getEntities().getExp());
                }
            }
        }
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public Collection<DiagramObject> getHoveredTarget(Coordinate p) {
        double f = 1 / this.diagramStatus.getFactor();
        return diagramObjects.getItems(new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
    }

    public DiagramContent getContent() {
        return content;
    }

    public DiagramStatus getDiagramStatus() {
        return diagramStatus;
    }

    public Collection<DiagramObject> getVisibleElements(int width, int height) {
        Box visibleArea = this.diagramStatus.getVisibleModelArea(width, height);
        return this.diagramObjects.getItems(visibleArea);
    }

    public Box getVisibleModelArea(int width, int height) {
        return this.diagramStatus.getVisibleModelArea(width, height);
    }

    public ColourProfileType getColourProfileType() {
        if (content.getForNormalDraw() == null || content.getForNormalDraw()) {
            return ColourProfileType.NORMAL;
        } else {
            return ColourProfileType.FADE_OUT;
        }
    }

    public void hideDialogs() {
        for (ContextDialogPanel dialogPanel : dialogMap.values()) {
            dialogPanel.hide();
        }
    }

    public void restoreDialogs() {
        for (ContextDialogPanel dialogPanel : dialogMap.values()) {
            dialogPanel.restore();
        }
    }

    public void showDialog(EventBus eventBus, DiagramObject item, Widget canvas) {
        if (item == null) return;
        if (!dialogMap.containsKey(item.getGraphObject())) {
            dialogMap.put(item.getGraphObject(), new ContextDialogPanel(eventBus, item, this, canvas));
        } else {
            dialogMap.get(item.getGraphObject()).show(true);
        }
    }

    @Override
    public String toString() {
        return "DiagramContext{" +
                "content=" + content +
                ", status=" + diagramStatus +
                '}';
    }

    private QuadTree<DiagramObject> createDiagramObjectTree(Collection<DiagramObject> diagramObjects, int elements, int step) {
        try {
            QuadTree<DiagramObject> quadTree = new QuadTree<>(content.minX, content.minY, content.maxX, content.maxY, elements);
            for (DiagramObject node : diagramObjects) {
                quadTree.add(node);
            }
            if (elements > NUMBER_OF_ELEMENTS) {
                Console.warn(this.content.getStableId() + " >> QuadTree for diagram objects created with quadrants of " + elements + " elements.");
            }
            return quadTree;
        } catch (RuntimeException e) {
            return createDiagramObjectTree(diagramObjects, elements + step, step);
        }
    }

    private QuadTree<DiagramInteractor> createInteractorTree(Collection<DiagramInteractor> interactors, int elements, int step) {
        try {
            QuadTree<DiagramInteractor> quadTree = new QuadTree<>(content.minX, content.minY, content.maxX, content.maxY, elements);
            for (DiagramInteractor interactor : interactors) {
                quadTree.add(interactor);
            }
            if (elements > NUMBER_OF_ELEMENTS) {
                Console.warn(this.content.getStableId() + " >> QuadTree for interactors created with quadrants of " + elements + " elements.");
            }
            return quadTree;
        } catch (RuntimeException e) {
            return this.createInteractorTree(interactors, elements + step, step);
        }
    }
}
