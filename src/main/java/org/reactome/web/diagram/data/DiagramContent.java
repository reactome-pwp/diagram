package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.graph.model.Pathway;
import org.reactome.web.diagram.data.graph.model.PhysicalEntity;
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
    Map<Long, DatabaseObject> dbId2DbObject;
    Map<String, DatabaseObject> stId2DbObject;
    MapSet<String, DatabaseObject> identifierMap;
    Set<Pathway> encapsulatedPathways;

    Set<DiagramObject> diseaseComponents;
    Set<DiagramObject> lofNodes;

    boolean graphLoaded = false;

    double minX; double maxX;
    double minY; double maxY;

    public DiagramContent() {
        this.diagramObjectMap = new TreeMap<Long, DiagramObject>();
        this.dbId2DbObject = new HashMap<Long, DatabaseObject>();
        this.stId2DbObject = new HashMap<String, DatabaseObject>();
        this.identifierMap = new MapSet<String, DatabaseObject>();
        this.encapsulatedPathways = new HashSet<Pathway>();
    }

    public void cache(DatabaseObject dbObject){
        this.graphLoaded = true;
        if(dbObject.getDbId()!=null) {
            dbId2DbObject.put(dbObject.getDbId(), dbObject);
        }
        if(dbObject.getStId()!=null) {
            stId2DbObject.put(dbObject.getStId(), dbObject);
        }

        if(dbObject instanceof PhysicalEntity){
            PhysicalEntity pe = (PhysicalEntity) dbObject;
            if(pe.getIdentifier()!=null) {
                identifierMap.add(pe.getIdentifier(), dbObject);
            }
        }else if(dbObject instanceof Pathway){
            encapsulatedPathways.add((Pathway) dbObject);
        }
    }

    public boolean containsOnlyEncapsultedPathways(){
        return (dbId2DbObject.values().size() == encapsulatedPathways.size());
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

//    public Set<DiagramObject> getNormalComponents() {
//        return normalComponents;
//    }

    public Set<DiagramObject> getDiseaseComponents() {
        return diseaseComponents;
    }

    public Set<DiagramObject> getLofNodes() {
        return lofNodes;
    }

    public DatabaseObject getDatabaseObject(String stId){
        return this.stId2DbObject.get(stId);
    }

    public DatabaseObject getDatabaseObject(Long dbId){
        return this.dbId2DbObject.get(dbId);
    }

    public DiagramObject getDiagramObject(Long id){
        return this.diagramObjectMap.get(id);
    }

    public Collection<DatabaseObject> getDatabaseObjects(){
        return this.dbId2DbObject.values();
    }

    public Collection<DiagramObject> getDiagramObjects(){
        return this.diagramObjectMap.values();
    }

    public MapSet<String, DatabaseObject> getIdentifierMap() {
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
