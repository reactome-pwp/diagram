package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.MapSet;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramContent {

    Long dbId;
    String stableId;
    String displayName;

    Long nextId;
    Boolean isDisease;
    Boolean forNormalDraw;
    Boolean hideCompartmentInName;

    Map<Long, DiagramObject> diagramObjectMap;
    Map<String, GraphObject> graphObjectCache;
    MapSet<String, GraphObject> identifierMap;
    Set<GraphPathway> encapsulatedPathways;
    Set<GraphSubpathway> subpathways;

    Set<DiagramObject> diseaseComponents;
    Set<DiagramObject> lofNodes;

    boolean graphLoaded = false;

    double minX; double maxX;
    double minY; double maxY;

    public DiagramContent() {
        this.diagramObjectMap = new TreeMap<>();
        this.graphObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        this.encapsulatedPathways = new HashSet<>();
        this.subpathways = new HashSet<>();
    }

    public void cache(GraphObject dbObject){
        this.graphLoaded = true;
        if(dbObject.getDbId()!=null) {
            graphObjectCache.put(dbObject.getDbId() + "", dbObject);
        }
        if(dbObject.getStId()!=null) {
            graphObjectCache.put(dbObject.getStId(), dbObject);
        }

        if(dbObject instanceof GraphPhysicalEntity){
            GraphPhysicalEntity pe = (GraphPhysicalEntity) dbObject;
            if(pe.getIdentifier()!=null) {
                identifierMap.add(pe.getIdentifier(), dbObject);
            }
        }else if(dbObject instanceof GraphPathway){
            encapsulatedPathways.add((GraphPathway) dbObject);
        }

        if (dbObject instanceof GraphSubpathway){
            this.subpathways.add((GraphSubpathway) dbObject);
        }
    }

    public void cache(List<? extends DiagramObject> diagramObjects){
        if(diagramObjects==null) return;
        for (DiagramObject diagramObject : diagramObjects) {
            this.diagramObjectMap.put(diagramObject.getId(), diagramObject);
        }
    }

    public boolean containsOnlyEncapsultedPathways(){
        return (getDatabaseObjects().size() == encapsulatedPathways.size());
    }

    public boolean containsEncapsultedPathways(){
        return encapsulatedPathways.size() > 0;
    }

    public boolean isGraphLoaded() {
        return graphLoaded;
    }

    public Long getDbId() {
        return dbId;
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

    public Long getNextId() {
        return nextId;
    }

    public Boolean getIsDisease() {
        return isDisease;
    }

    public Boolean getForNormalDraw() {
        return forNormalDraw;
    }

    public Boolean getHideCompartmentInName() {
        return hideCompartmentInName;
    }

    public Set<DiagramObject> getDiseaseComponents() {
        return diseaseComponents;
    }

    public Set<DiagramObject> getLofNodes() {
        return lofNodes;
    }

    public GraphObject getDatabaseObject(String stId){
        return this.graphObjectCache.get(stId);
    }

    public GraphObject getDatabaseObject(Long dbId){
        return this.graphObjectCache.get(dbId.toString());
    }

    public DiagramObject getDiagramObject(Long id){
        return this.diagramObjectMap.get(id);
    }

    public Collection<GraphObject> getDatabaseObjects(){
        return new HashSet<>(this.graphObjectCache.values());
    }

    public Collection<DiagramObject> getDiagramObjects(){
        return this.diagramObjectMap.values();
    }

    public MapSet<String, GraphObject> getIdentifierMap() {
        return identifierMap;
    }

    public Set<GraphSubpathway> getSubpathways() {
        return subpathways;
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

    public double getWidth(){
        return maxX - minX;
    }

    public double getHeight(){
        return maxY - minY;
    }

    @Override
    public String toString() {
        return "DiagramContent{" +
                "stableId='" + stableId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
