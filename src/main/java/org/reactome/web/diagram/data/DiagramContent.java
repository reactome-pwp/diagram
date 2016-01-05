package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
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
    Map<String, GraphSubpathway> subpathwaysCache;
    MapSet<String, GraphObject> identifierMap;
    Set<GraphPathway> encapsulatedPathways;
    Map<String, DiagramInteractor> interactorMap;

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
        this.subpathwaysCache = new HashMap<>();
        this.interactorMap = new HashMap<>();
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

    public void cache(InteractorEntity interactor) {
        if (interactor.getAccession() != null) {
            this.interactorMap.put(interactor.getAccession(), interactor);
        }
    }

    public void clearInteractors() {
        for (DiagramInteractor diagramInteractor : interactorMap.values()) {
            if(diagramInteractor instanceof InteractorEntity){
                InteractorEntity interactor = (InteractorEntity) diagramInteractor;
                for (GraphPhysicalEntity pe : interactor.getInteractsWith()) {
                    pe.initInteractors(); //It could happen that the same pe is called more than once -> not a problem
                }
            }
        }
        for (DiagramObject diagramObject : diagramObjectMap.values()) {
            if (diagramObject instanceof Node) {
                Node node = (Node) diagramObject;
                SummaryItem interactorsSummary = node.getInteractorsSummary();
                if (interactorsSummary != null) {
                    interactorsSummary.setNumber(null);
                }
            }
        }
        this.interactorMap = new HashMap<>();
    }

    public void setInteractors(String acc, Integer number) {
        Set<GraphObject> elements = identifierMap.getElements(acc);
        if (elements != null) {
            for (GraphObject graphObject : elements) {
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (DiagramObject diagramObject : pe.getDiagramObjects()) {
                        Node node = (Node) diagramObject;
                        node.getInteractorsSummary().setNumber(number);
                    }
                }
            }
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
        return this.subpathwaysCache.get(dbId.toString());
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

    public Collection<DiagramInteractor> getDiagramInteractors(String resource) {
        return this.interactorMap.values();
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

    @Override
    public String toString() {
        return "DiagramContent{" +
                "stableId='" + stableId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
