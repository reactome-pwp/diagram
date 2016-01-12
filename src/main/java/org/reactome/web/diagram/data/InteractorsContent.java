package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.MapSet;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsContent {

    static Map<String, Double> interactorsThreshold = new HashMap<>();

    Map<String, MapSet<String, RawInteractor>> rawInteractorsCache;

    MapSet<String, InteractorsSummary> interactorsSummaryMap; //resource -> InteractorsSummary
    Map<String, Map<String, InteractorEntity>> interactorsCache; //resource -> acc -> interactors
    Map<String, MapSet<String, DiagramInteractor>> interactorsPerAcc; //resource -> node acc -> interactors
    Map<String, MapSet<Node, InteractorLink>> interactionsPerNode; //resource -> layout node -> interaction

    public InteractorsContent() {
        this.interactorsSummaryMap = new MapSet<>();
        this.interactorsCache = new HashMap<>();
        this.interactorsPerAcc = new HashMap<>();
        this.interactionsPerNode = new HashMap<>();
    }

    public void cache(String resource, InteractorEntity interactor) {
        if (interactor.getAccession() != null) {
            Map<String, InteractorEntity> map = this.interactorsCache.get(resource);
            if(map == null){
                map = new HashMap<>();
                this.interactorsCache.put(resource, map);
            }
            map.put(interactor.getAccession(), interactor);
        }
    }

    public void cache(String resource, Node node, DiagramInteractor diagramInteractor){
        if(diagramInteractor instanceof InteractorLink) {
            MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
            if (cache == null) {
                cache = new MapSet<>();
                interactionsPerNode.put(resource, cache);
            }
            cache.add(node, (InteractorLink) diagramInteractor);
        }

        MapSet<String, DiagramInteractor> aux = interactorsPerAcc.get(resource);
        if (aux == null) {
            aux = new MapSet<>();
            interactorsPerAcc.put(resource, aux);
        }
        GraphPhysicalEntity pe = node.getGraphObject();
        aux.add(pe.getIdentifier(), diagramInteractor);
    }

    public Collection<InteractorEntity> getDiagramInteractors(String resource) {
        Map<String, InteractorEntity> cache = interactorsCache.get(resource);
        if (cache != null) return cache.values();
        return new HashSet<>();
    }

    public Collection<DiagramInteractor> getDiagramInteractors(String resource, String acc) {
        MapSet<String, DiagramInteractor> cache = interactorsPerAcc.get(resource);
        if (cache != null) {
            return cache.getElements(acc);
        }
        return new HashSet<>();
    }

    public Collection<InteractorLink> getDiagramInteractions(String resource) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
        if (cache != null) return cache.values();
        return new HashSet<>();
    }

    public Collection<InteractorLink> getDiagramInteractions(String resource, Node node) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
        if (cache != null) return cache.getElements(node);
        return new HashSet<>();
    }

    public InteractorEntity getDiagramInteractor(String resource, String acc) {
        Map<String, InteractorEntity> cache = interactorsCache.get(resource);
        if (cache != null) return cache.get(acc);
        return null;
    }

    public void cacheInteractors(String resource, String acc, Integer number, MapSet<String, GraphObject> identifierMap) {
        InteractorsSummary summary = new InteractorsSummary(acc, number);
        this.interactorsSummaryMap.add(resource.toLowerCase(), summary);
        setInteractorsSummary(summary, identifierMap);
    }

    public boolean isInteractorResourceCached(String resource) {
        return interactorsSummaryMap.keySet().contains(resource.toLowerCase());
    }

    public int getNumberOfBustEntities(String resource){
        int rtn = 0;
        for (InteractorEntity entity : getDiagramInteractors(resource)) {
            if(entity.isVisible()) rtn++;
        }
        return rtn;
    }

    public void resetBurstInteractors(String resource, Collection<DiagramObject> diagramObjects){
        Set<InteractorsSummary> summaries = interactorsSummaryMap.getElements(resource.toLowerCase());
        if(summaries!=null) {
            for (InteractorsSummary summary : summaries) {
                summary.setPressed(false);
            }
        }
        for (DiagramObject diagramObject : diagramObjects) {
            if(diagramObject instanceof Node){
                Node node = (Node) diagramObject;
                SummaryItem summaryItem = node.getInteractorsSummary();
                if(summaryItem!=null){
                    summaryItem.setPressed(null);
                }
            }
        }
    }

    public void restoreInteractorsSummary(String resource, MapSet<String, GraphObject> identifierMap){
        Set<InteractorsSummary> items = interactorsSummaryMap.getElements(resource.toLowerCase());
        if(items==null) return;
        for (InteractorsSummary summary : items) {
            setInteractorsSummary(summary, identifierMap);
        }
    }

    public static double getInteractorsThreshold(String resource){
        Double threshold = interactorsThreshold.get(resource);
        if (threshold == null) {
            threshold = 0.5;
            setInteractorsThreshold(resource, threshold);
        }
        return threshold;
    }

    public static void setInteractorsThreshold(String resource, double threshold){
        interactorsThreshold.put(resource, threshold);
    }

    private void setInteractorsSummary(InteractorsSummary summary, MapSet<String, GraphObject> identifierMap){
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
}
