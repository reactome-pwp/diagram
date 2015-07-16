package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.graph.model.Pathway;
import org.reactome.web.diagram.data.graph.model.PhysicalEntity;
import org.reactome.web.diagram.data.graph.model.Subpathway;
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
    Map<String, DatabaseObject> databaseObjectCache;
    MapSet<String, DatabaseObject> identifierMap;
    Set<Pathway> encapsulatedPathways;
    Set<Subpathway> subpathways;

    Set<DiagramObject> diseaseComponents;
    Set<DiagramObject> lofNodes;

    boolean graphLoaded = false;

    double minX; double maxX;
    double minY; double maxY;

    public DiagramContent() {
        this.diagramObjectMap = new TreeMap<>();
        this.databaseObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        this.encapsulatedPathways = new HashSet<>();
        this.subpathways = new HashSet<>();
    }

    public void cache(DatabaseObject dbObject){
        this.graphLoaded = true;
        if(dbObject.getDbId()!=null) {
            databaseObjectCache.put(dbObject.getDbId()+"", dbObject);
        }
        if(dbObject.getStId()!=null) {
            databaseObjectCache.put(dbObject.getStId(), dbObject);
        }

        if(dbObject instanceof PhysicalEntity){
            PhysicalEntity pe = (PhysicalEntity) dbObject;
            if(pe.getIdentifier()!=null) {
                identifierMap.add(pe.getIdentifier(), dbObject);
            }
        }else if(dbObject instanceof Pathway){
            encapsulatedPathways.add((Pathway) dbObject);
        }

        if (dbObject instanceof Subpathway){
            this.subpathways.add((Subpathway) dbObject);
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

    public Set<Pathway> getEncapsulatedPathways() {
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

    public DatabaseObject getDatabaseObject(String stId){
        return this.databaseObjectCache.get(stId);
    }

    public DatabaseObject getDatabaseObject(Long dbId){
        return this.databaseObjectCache.get(dbId.toString());
    }

    public DiagramObject getDiagramObject(Long id){
        return this.diagramObjectMap.get(id);
    }

    public Collection<DatabaseObject> getDatabaseObjects(){
        return new HashSet<>(this.databaseObjectCache.values());
    }

    public Collection<DiagramObject> getDiagramObjects(){
        return this.diagramObjectMap.values();
    }

    public MapSet<String, DatabaseObject> getIdentifierMap() {
        return identifierMap;
    }

    public Set<Subpathway> getSubpathways() {
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
