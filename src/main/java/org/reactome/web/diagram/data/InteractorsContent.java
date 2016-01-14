package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.util.LruCache;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTree;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsContent {

    static final int INTERACTORS_RESOURCE_CACHE_SIZE = 5;
    static final int INTERACTORS_FRAME_OFFSET = 500;

    //The number of elements for every QuadTree quadrant node
    static final int NUMBER_OF_ELEMENTS = 15;
    static final int MIN_AREA = 25;

    static Map<String, Double> interactorsThreshold = new HashMap<>();

    Map<String, MapSet<String, RawInteractor>> rawInteractorsCache; //resource -> node acc -> raw interactors

    MapSet<String, InteractorsSummary> interactorsSummaryMap; //resource -> InteractorsSummary
    Map<String, Map<String, InteractorEntity>> interactorsCache; //resource -> acc -> interactors
    Map<String, MapSet<String, DiagramInteractor>> interactorsPerAcc; //resource -> node acc -> interactors
    Map<String, MapSet<Node, InteractorLink>> interactionsPerNode; //resource -> layout node -> interaction

    private LruCache<String, QuadTree<DiagramInteractor>> interactorsTreeCache;
    private double minX, minY, maxX, maxY;

    public InteractorsContent(double minX, double minY, double maxX, double maxY) {
        this.rawInteractorsCache = new HashMap<>();
        this.interactorsSummaryMap = new MapSet<>();
        this.interactorsCache = new HashMap<>();
        this.interactorsPerAcc = new HashMap<>();
        this.interactionsPerNode = new HashMap<>();

        this.interactorsTreeCache = new LruCache<>(INTERACTORS_RESOURCE_CACHE_SIZE);
        this.minX = minX - INTERACTORS_FRAME_OFFSET;
        this.maxX = maxX + INTERACTORS_FRAME_OFFSET;
        this.minY = minY - INTERACTORS_FRAME_OFFSET;
        this.maxY = maxY + INTERACTORS_FRAME_OFFSET;
    }

    public void cache(String resource, String acc, RawInteractor rawInteractor) {
        getOrCreateRawInteractorCachedResource(resource).add(acc, rawInteractor);
    }

    public MapSet<String, RawInteractor> getOrCreateRawInteractorCachedResource(String resource){
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.toLowerCase());
        if (map == null) {
            map = new MapSet<>();
            rawInteractorsCache.put(resource.toLowerCase(), map);
        }
        return map;
    }

    public void cache(String resource, InteractorEntity interactor) {
        if (interactor.getAccession() != null) {
            Map<String, InteractorEntity> map = this.interactorsCache.get(resource);
            if (map == null) {
                map = new HashMap<>();
                this.interactorsCache.put(resource, map);
            }
            map.put(interactor.getAccession(), interactor);
        }
    }

    public void cache(String resource, Node node, DiagramInteractor diagramInteractor) {
        if (diagramInteractor instanceof InteractorLink) {
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

    public void cacheInteractors(String resource, String acc, Integer number, MapSet<String, GraphObject> identifierMap) {
        InteractorsSummary summary = new InteractorsSummary(acc, number);
        this.interactorsSummaryMap.add(resource.toLowerCase(), summary);
        setInteractorsSummary(summary, identifierMap);
    }


    //This method is not checking whether the interactors where previously put in place since
    //when it is called, the interactors have probably been retrieved "again" from the server
    //IMPORTANT: To avoid loading data that already exists -> CHECK BEFORE RETRIEVING :)
    public void addInteractor(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource.toLowerCase());
        if(tree==null) {
            tree = new QuadTree<>(minX, minY, maxX, maxY, NUMBER_OF_ELEMENTS, MIN_AREA);
            interactorsTreeCache.put(resource.toLowerCase(), tree);
        }
        tree.add(interactor);
    }

    public void updateInteractor(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource.toLowerCase());
        if (tree != null){
            tree.remove(interactor);
            tree.add(interactor);
        }
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


    public Collection<DiagramInteractor> getHoveredTarget(String resource, Coordinate p, double factor) {
        if(resource!=null) {
            QuadTree<DiagramInteractor> quadTree = this.interactorsTreeCache.get(resource.toLowerCase());
            if (quadTree != null) {
                double f = 1 / factor;
                return quadTree.getItems(new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
            }
        }
        return new HashSet<>();
    }

    public Set<RawInteractor> getRawInteractors(String resource, String acc){
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.toLowerCase());
        if (map != null) {
            return map.getElements(acc);
        }
        return null;
    }

    public boolean isResourceLoaded(String resource){
        return rawInteractorsCache.keySet().contains(resource.toLowerCase());
    }

    public Collection<DiagramInteractor> getVisibleInteractors(String resource, Box visibleArea) {
        if(resource!=null) {
            QuadTree<DiagramInteractor> quadTree = this.interactorsTreeCache.get(resource.toLowerCase());
            if (quadTree != null) {
                return quadTree.getItems(visibleArea);
            }
        }
        return new HashSet<>();
    }

    public boolean isInteractorResourceCached(String resource) {
        return interactorsSummaryMap.keySet().contains(resource.toLowerCase());
    }

    public int getNumberOfBustEntities(String resource) {
        int rtn = 0;
        for (InteractorEntity entity : getDiagramInteractors(resource)) {
            if (entity.isVisible()) rtn++;
        }
        return rtn;
    }

    public void resetBurstInteractors(String resource, Collection<DiagramObject> diagramObjects) {
        Set<InteractorsSummary> summaries = interactorsSummaryMap.getElements(resource.toLowerCase());
        if (summaries != null) {
            for (InteractorsSummary summary : summaries) {
                summary.setPressed(false);
            }
        }
        for (DiagramObject diagramObject : diagramObjects) {
            if (diagramObject instanceof Node) {
                Node node = (Node) diagramObject;
                SummaryItem summaryItem = node.getInteractorsSummary();
                if (summaryItem != null) {
                    summaryItem.setPressed(null);
                }
            }
        }
    }

    public void restoreInteractorsSummary(String resource, MapSet<String, GraphObject> identifierMap) {
        Set<InteractorsSummary> items = interactorsSummaryMap.getElements(resource.toLowerCase());
        if (items == null) return;
        for (InteractorsSummary summary : items) {
            setInteractorsSummary(summary, identifierMap);
        }
    }

    public static double getInteractorsThreshold(String resource) {
        Double threshold = interactorsThreshold.get(resource);
        if (threshold == null) {
            threshold = 0.5;
            setInteractorsThreshold(resource, threshold);
        }
        return threshold;
    }

    public static void setInteractorsThreshold(String resource, double threshold) {
        interactorsThreshold.put(resource, threshold);
    }

    private void setInteractorsSummary(InteractorsSummary summary, MapSet<String, GraphObject> identifierMap) {
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
