package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
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

    //INTERACTORS
    MapSet<String, InteractorsSummary> interactorsSummaryMap;
//    Map<String, DiagramInteractor> interactorMap;

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

        this.interactorsSummaryMap = new MapSet<>();
//        this.interactorMap = new HashMap<>();
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
//            this.interactorMap.put(interactor.getAccession(), interactor);
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

    public MapSet<String, GraphObject> getIdentifierMap() {
        return identifierMap;
    }

    public Collection<DiagramObject> getDiagramObjectsWithCommonIdentifier(DiagramObject diagramObject){
        Set<DiagramObject> rtn = new HashSet<>();
        GraphObject graphObject = diagramObject.getGraphObject();
        if(graphObject instanceof GraphPhysicalEntity){
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            for (GraphObject object : identifierMap.getElements(pe.getIdentifier())) {
                rtn.addAll(object.getDiagramObjects());
            }
        }
        return rtn;
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

//    public Collection<DiagramInteractor> getDiagramInteractors() {
//        return this.interactorMap.values();
//    }

//    public DiagramInteractor getDiagramInteractor(String acc){
//        return this.interactorMap.get(acc);
//    }

    public void cacheInteractors(String resource, String acc, Integer number) {
        InteractorsSummary summary = new InteractorsSummary(acc, number);
        this.interactorsSummaryMap.add(resource.toLowerCase(), summary);
        setInteractors(summary);
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


    public boolean isInteractorResourceCached(String resource) {
        return interactorsSummaryMap.keySet().contains(resource.toLowerCase());
    }

    public int getNumberOfBustEntities(String resource){
        int rtn = 0;
        if (resource != null) {
            Set<InteractorsSummary> set = interactorsSummaryMap.getElements(resource.toLowerCase());
            if (set != null) {
                for (InteractorsSummary summary : set) {
                    if (summary.isPressed()) rtn++;
                }
            }
        }
        return rtn;
    }

    public void resetBurstInteractors(){
        for (String resource : interactorsSummaryMap.keySet()) {
            for (InteractorsSummary summary : interactorsSummaryMap.getElements(resource)) {
                summary.setPressed(false);
            }
        }
        for (DiagramObject diagramObject : getDiagramObjects()) {
            if(diagramObject instanceof Node){
                Node node = (Node) diagramObject;
                node.getInteractorsSummary().setPressed(null);
            }
        }
    }

    public void restoreInteractors(String resource){
        Set<InteractorsSummary> items = interactorsSummaryMap.getElements(resource.toLowerCase());
        if(items==null) return;
        for (InteractorsSummary summary : items) {
            setInteractors(summary);
        }
    }

    private void setInteractors(InteractorsSummary summary) {
        Set<GraphObject> elements = identifierMap.getElements(summary.getAccession());
        if (elements != null) {
            for (GraphObject graphObject : elements) {
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (DiagramObject diagramObject : pe.getDiagramObjects()) {
                        Node node = (Node) diagramObject;
                        node.getInteractorsSummary().setNumber(summary.getNumber());
                        node.getInteractorsSummary().setPressed(summary.isPressed());
                        //The changes need to be updated in the cache, so when restoring, the pressed ones are known
                        node.setDiagramEntityInteractorsSummary(summary);
                    }
                }
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
