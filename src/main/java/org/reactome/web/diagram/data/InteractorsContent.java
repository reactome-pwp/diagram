package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.Console;
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

    private Map<String, MapSet<String, RawInteractor>> rawInteractorsCache; //resource -> node acc -> raw interactors

    private MapSet<String, InteractorsSummary> interactorsSummaryMap; //resource -> InteractorsSummary
    private Map<String, Map<String, InteractorEntity>> interactorsCache; //resource -> acc -> interactors
    private Map<String, MapSet<String, DiagramInteractor>> interactorsPerAcc; //resource -> node acc -> interactors
    private Map<String, MapSet<Node, InteractorLink>> interactionsPerNode; //resource -> layout node -> interaction

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

    public MapSet<String, RawInteractor> getOrCreateRawInteractorCachedResource(String resource) {
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.toLowerCase());
        if (map == null) {
            map = new MapSet<>();
            rawInteractorsCache.put(resource.toLowerCase(), map);
        }
        return map;
    }

    public void cache(String resource, InteractorEntity interactor) {
        if (interactor.getAccession() != null) {
            Map<String, InteractorEntity> map = this.interactorsCache.get(resource.toLowerCase());
            if (map == null) {
                map = new HashMap<>();
                this.interactorsCache.put(resource.toLowerCase(), map);
            }
            map.put(interactor.getAccession(), interactor);
        }
    }

    public void cache(String resource, Node node, DiagramInteractor diagramInteractor) {
        if (diagramInteractor instanceof InteractorLink) {
            MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource.toLowerCase());
            if (cache == null) {
                cache = new MapSet<>();
                interactionsPerNode.put(resource.toLowerCase(), cache);
            }
            cache.add(node, (InteractorLink) diagramInteractor);
        }

        MapSet<String, DiagramInteractor> aux = interactorsPerAcc.get(resource.toLowerCase());
        if (aux == null) {
            aux = new MapSet<>();
            interactorsPerAcc.put(resource.toLowerCase(), aux);
        }
        GraphPhysicalEntity pe = node.getGraphObject();
        aux.add(pe.getIdentifier(), diagramInteractor);
    }

    public void cacheInteractors(String resource, String acc, Integer number, MapSet<String, GraphObject> identifierMap) {
        if(number == 0) return;
        Set<GraphObject> elements = identifierMap.getElements(acc);
        if (elements != null) {
            for (GraphObject graphObject : elements) {
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (DiagramObject diagramObject : pe.getDiagramObjects()) {
                        InteractorsSummary summary = new InteractorsSummary(acc, diagramObject.getId(), number);
                        this.interactorsSummaryMap.add(resource.toLowerCase(), summary);
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


    //This method is not checking whether the interactors where previously put in place since
    //when it is called, the interactors have probably been retrieved "again" from the server
    //IMPORTANT: To avoid loading data that already exists -> CHECK BEFORE RETRIEVING :)
    public void addInteractor(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource.toLowerCase());
        if (tree == null) {
            tree = new QuadTree<>(minX, minY, maxX, maxY, NUMBER_OF_ELEMENTS, MIN_AREA);
            interactorsTreeCache.put(resource.toLowerCase(), tree);
        }
        tree.add(interactor);
    }

    public void updateInteractor(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource.toLowerCase());
        if (tree != null) {
            tree.remove(interactor);
            tree.add(interactor);
        }
    }

    public Collection<InteractorEntity> getDiagramInteractors(String resource) {
        Map<String, InteractorEntity> cache = interactorsCache.get(resource.toLowerCase());
        if (cache != null) return cache.values();
        return new HashSet<>();
    }

    public Collection<DiagramInteractor> getDiagramInteractors(String resource, String acc) {
        MapSet<String, DiagramInteractor> cache = interactorsPerAcc.get(resource.toLowerCase());
        if (cache != null) {
            return cache.getElements(acc);
        }
        return new HashSet<>();
    }

    public Collection<InteractorLink> getDiagramInteractions(String resource) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource.toLowerCase());
        if (cache != null) return cache.values();
        return new HashSet<>();
    }

    public List<InteractorLink> getDiagramInteractions(String resource, Node node) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource.toLowerCase());
        if (cache != null){
            Set<InteractorLink> set = cache.getElements(node);
            if (set != null) {
                List<InteractorLink> rtn = new ArrayList<>(set);
                Collections.sort(rtn);
                return rtn;
            }
        }
        return null;
    }

    public InteractorEntity getDiagramInteractor(String resource, String acc) {
        Map<String, InteractorEntity> cache = interactorsCache.get(resource.toLowerCase());
        if (cache != null) return cache.get(acc);
        return null;
    }


    public Collection<DiagramInteractor> getHoveredTarget(String resource, Coordinate p, double factor) {
        double f = 1 / factor;
        return getVisibleInteractors(resource, new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
    }

    //We keep this cache to avoid creating it every time
    private Map<String, List<InteractorSearchResult>> interactorsSearchItemsPerResource = new HashMap<>();
    public List<InteractorSearchResult> getInteractorSearchResult(String resource, DiagramContent content) {
        List<InteractorSearchResult> rtn = interactorsSearchItemsPerResource.get(resource);
        if (rtn != null) return rtn;
        rtn = new ArrayList<>();
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.toLowerCase());
        if (map != null) {
            Map<String, InteractorSearchResult> cache = new HashMap<>();
            for (String diagramAcc : map.keySet()) {
                for (RawInteractor rawInteractor : map.getElements(diagramAcc)) {
                    String accession = rawInteractor.getAcc();
                    InteractorSearchResult result = cache.get(accession);
                    if (result == null) {
                        result = new InteractorSearchResult(resource, accession);
                        cache.put(accession, result);
                        rtn.add(result);
                    }
                    result.addInteractsWith(rawInteractor.getId(), getInteractsWith(diagramAcc, content));
                    result.addInteraction(rawInteractor);
                }
            }
        }
        interactorsSearchItemsPerResource.put(resource, rtn);
        return rtn;
    }

    private Set<GraphObject> getInteractsWith(String diagramAcc, DiagramContent content) {
        Set<GraphObject> aux  = content.getIdentifierMap().getElements(diagramAcc);
        if(aux != null) return aux;
        return new HashSet<>();
    }

    public List<RawInteractor> getRawInteractors(String resource, String acc) {
        List<RawInteractor> rtn = new ArrayList<>();
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.toLowerCase());
        if (map != null) {
            rtn.addAll(map.getElements(acc));
            Collections.sort(rtn, new Comparator<RawInteractor>() {
                @Override
                public int compare(RawInteractor o1, RawInteractor o2) {
                    int c = Double.compare(o2.getScore(), o1.getScore());
                    if (c == 0) return o1.getAcc().compareTo(o2.getAcc());
                    return c;
                }
            });
        }
        return rtn;
    }

    public boolean isResourceLoaded(String resource) {
        return rawInteractorsCache.keySet().contains(resource.toLowerCase());
    }

    public Collection<DiagramInteractor> getVisibleInteractors(String resource, Box visibleArea) {
        Set<DiagramInteractor> rtn = new HashSet<>();
        if (resource != null) {
            QuadTree<DiagramInteractor> quadTree = this.interactorsTreeCache.get(resource.toLowerCase());
            if (quadTree != null) {
                rtn = quadTree.getItems(visibleArea);
            }
        }
        return rtn;
    }

    public boolean isInteractorResourceCached(String resource) {
        return interactorsSummaryMap.keySet().contains(resource.toLowerCase());
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

    public void restoreInteractorsSummary(String resource, DiagramContent content) {
        Set<InteractorsSummary> items = interactorsSummaryMap.getElements(resource.toLowerCase());
        if (items == null) return;
        for (InteractorsSummary summary : items) {
            Console.info(content.getDiagramObject(summary.getDiagramId()));
            Node node = (Node) content.getDiagramObject(summary.getDiagramId());
            node.getInteractorsSummary().setNumber(summary.getNumber());
            node.getInteractorsSummary().setPressed(summary.isPressed());
            //The changes need to be updated in the cache, so when restoring, the pressed ones are known
            node.setDiagramEntityInteractorsSummary(summary);
        }
    }

    public static double getInteractorsThreshold(String resource) {
        Double threshold = interactorsThreshold.get(resource.toLowerCase());
        if (threshold == null) {
            threshold = 0.5;
            setInteractorsThreshold(resource, threshold);
        }
        return threshold;
    }

    public static void setInteractorsThreshold(String resource, double threshold) {
        interactorsThreshold.put(resource.toLowerCase(), threshold);
    }
}
