package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTree;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramContent {

    //The number of elements for every QuadTree quadrant node
    static final int NUMBER_OF_ELEMENTS = 25;
    //Quadrant minimum area (width * height):             180
    //  Right now an area of 180 x 80 = 14400 would     [--][--] 8
    //  host 4 entities of 90x40 each                   [--][--] 0
    //  An area of 90,000 includes 25 entities
    static final int MIN_AREA = 90000;

    Long dbId;
    String stableId;
    String displayName;

    Boolean isDisease;
    Boolean forNormalDraw;
    Boolean picture;

    Map<Long, DiagramObject> diagramObjectMap;
    Map<String, GraphObject> graphObjectCache;
    Map<String, GraphSubpathway> subpathwaysCache;
    MapSet<String, GraphObject> identifierMap;
    Set<GraphPathway> encapsulatedPathways;

    private QuadTree<DiagramObject> diagramObjects;

    boolean graphLoaded = false;

    double minX, maxX, minY, maxY;

    public DiagramContent() {
        this.diagramObjectMap = new TreeMap<>();
        this.graphObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        this.encapsulatedPathways = new HashSet<>();
        this.subpathwaysCache = new HashMap<>();
    }

    //Please note that the way the content is created is by injecting the values without the constructor
    //init has to be called to be called once every value has been set up
    DiagramContent init(){
        this.diagramObjects = new QuadTree<>(minX, minY, maxX, maxY, NUMBER_OF_ELEMENTS, MIN_AREA);
        for (DiagramObject diagramObject : getDiagramObjects()) {
            this.diagramObjects.add(diagramObject);
        }
        return this;
    }

    public void cache(GraphObject dbObject) {
        this.graphLoaded = true;
        if (dbObject.getDbId() != null) {
            graphObjectCache.put(dbObject.getDbId() + "", dbObject);
        }
        if (dbObject.getStId() != null) {
            graphObjectCache.put(dbObject.getStId(), dbObject);
        }

        if (dbObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) dbObject;
            if (pe.getIdentifier() != null) {
                identifierMap.add(pe.getIdentifier(), dbObject);
            }
            if (pe.getGeneNames() != null) {
                for (String gene : pe.getGeneNames()) {
                    identifierMap.add(gene, dbObject);
                }
            }
        } else if (dbObject instanceof GraphPathway) {
            encapsulatedPathways.add((GraphPathway) dbObject);
        }

        if (dbObject instanceof GraphSubpathway) {
            GraphSubpathway gsp = (GraphSubpathway) dbObject;
            this.subpathwaysCache.put("" + gsp.getDbId(), gsp);
            this.subpathwaysCache.put(gsp.getStId(), gsp);
        }
    }

    public void cache(List<? extends DiagramObject> diagramObjects) {
        if (diagramObjects == null) return;
        for (DiagramObject diagramObject : diagramObjects) {
            this.diagramObjectMap.put(diagramObject.getId(), diagramObject);
        }
    }

    public boolean containsOnlyEncapsulatedPathways() {
        return (getDatabaseObjects().size() == encapsulatedPathways.size());
    }

    public boolean containsEncapsulatedPathways() {
        return encapsulatedPathways.size() > 0;
    }

    public boolean isGraphLoaded() {
        return graphLoaded;
    }

    public Long getDbId() {
        return dbId;
    }

    public Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor) {
        double f = 1 / factor;
        return diagramObjects.getItems(new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
    }

    public String getStableId() {
        return stableId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<GraphPathway> getEncapsulatedPathways() {
        return encapsulatedPathways;
    }

    public Boolean getIsDisease() {
        return isDisease;
    }

    public Boolean getForNormalDraw() {
        return forNormalDraw;
    }

    public GraphObject getDatabaseObject(String identifier) {
        return this.graphObjectCache.get(identifier);
    }

    public GraphObject getDatabaseObject(Long dbId) {
        return this.graphObjectCache.get(dbId.toString());
    }

    public GraphSubpathway getGraphSubpathway(String stId) {
        return this.subpathwaysCache.get(stId);
    }

    public GraphSubpathway getGraphSubpathway(Long dbId) {
        return getGraphSubpathway(dbId.toString());
    }

    public DiagramObject getDiagramObject(Long id) {
        return this.diagramObjectMap.get(id);
    }

    public Collection<GraphObject> getDatabaseObjects() {
        return new HashSet<>(this.graphObjectCache.values());
    }

    public Collection<DiagramObject> getDiagramObjects() {
        return this.diagramObjectMap.values();
    }

    public MapSet<String, GraphObject> getIdentifierMap() {
        return identifierMap;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public Collection<DiagramObject> getVisibleItems(Box visibleArea) {
        return this.diagramObjects.getItems(visibleArea);
    }

    public int getNumberOfBurstEntities() {
        int n = 0;
        for (DiagramObject diagramObject : getDiagramObjects()) {
            if(diagramObject instanceof Node){
                Node node = (Node) diagramObject;
                SummaryItem summaryItem = node.getInteractorsSummary();
                if(summaryItem!=null){
                    if(summaryItem.getPressed()!=null && summaryItem.getPressed()) n++;
                }
            }
        }
        return n;
    }

    public void clearDisplayedInteractors() {
        for (DiagramObject diagramObject : getDiagramObjects()) {
            if (diagramObject instanceof Node) {
                Node node = (Node) diagramObject;
                SummaryItem interactorsSummary = node.getInteractorsSummary();
                if (interactorsSummary != null) {
                    interactorsSummary.setNumber(null);
                    interactorsSummary.setPressed(null);
                }
                node.setDiagramEntityInteractorsSummary(null);
            }
        }
    }

    @Override
    public String toString() {
        return "DiagramContent{" +
                "stableId='" + stableId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
