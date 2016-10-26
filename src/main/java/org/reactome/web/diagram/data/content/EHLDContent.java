package org.reactome.web.diagram.data.content;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class EHLDContent extends GenericContent {

    private Map<String, DiagramObject> tempDiagramObjectMap;
    private Map<Long, DiagramObject> diagramObjectMap;
//    private Map<String, GraphObject> graphObjectCache;
//    private Map<String, GraphSubpathway> subpathwaysCache;
//    private MapSet<String, GraphObject> identifierMap;
//    private Set<GraphPathway> encapsulatedPathways;

    public EHLDContent() {
        this.tempDiagramObjectMap = new HashMap<>();
        this.diagramObjectMap = new TreeMap<>();
//        this.graphObjectCache = new HashMap<>();
//        this.identifierMap = new MapSet<>();
//        this.encapsulatedPathways = new HashSet<>();
//        this.subpathwaysCache = new HashMap<>();
    }

    @Override
    public Content init() {
        return this;
    }

    @Override
    public void cache(GraphObject dbObject) {

    }

    @Override
    public void cache(List<? extends DiagramObject> diagramObjects) {
        if (diagramObjects == null) return;
        for (DiagramObject diagramObject : diagramObjects) {
            this.diagramObjectMap.put(diagramObject.getId(), diagramObject);
        }
    }



    @Override
    public boolean containsOnlyEncapsulatedPathways() {
        return false;
    }

    @Override
    public boolean containsEncapsulatedPathways() {
        return false;
    }

    @Override
    public Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor) {
        return null;
    }

    @Override
    public Set<GraphPathway> getEncapsulatedPathways() {
        return null;
    }

    @Override
    public GraphObject getDatabaseObject(String identifier) {
        return null;
    }

    @Override
    public GraphObject getDatabaseObject(Long dbId) {
        return null;
    }

    @Override
    public GraphSubpathway getGraphSubpathway(String stId) {
        return null;
    }

    @Override
    public GraphSubpathway getGraphSubpathway(Long dbId) {
        return null;
    }

    @Override
    public DiagramObject getDiagramObject(Long id) {
        return null;
    }

    @Override
    public Collection<GraphObject> getDatabaseObjects() {
        return null;
    }

    @Override
    public Collection<DiagramObject> getDiagramObjects() {
        return null;
    }

    @Override
    public MapSet<String, GraphObject> getIdentifierMap() {
        return null;
    }

    @Override
    public double getMinX() {
        return 0;
    }

    @Override
    public double getMaxX() {
        return 0;
    }

    @Override
    public double getMinY() {
        return 0;
    }

    @Override
    public double getMaxY() {
        return 0;
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public Collection<DiagramObject> getVisibleItems(Box visibleArea) {
        return null;
    }

    @Override
    public int getNumberOfBurstEntities() {
        return 0;
    }

    @Override
    public void clearDisplayedInteractors() {
        //Nothing Here
    }
}
