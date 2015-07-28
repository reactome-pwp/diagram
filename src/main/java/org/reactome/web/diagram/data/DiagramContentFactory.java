package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.SubpathwayRaw;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.*;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class DiagramContentFactory {

    public static DiagramContent getDiagramContent(Diagram diagram ) {
        DiagramContent content = new DiagramContent();

        //Read and set general pathway information
        content.nextId = diagram.getNextId();
        content.stableId = diagram.getStableId();
        content.displayName = diagram.getDisplayName();
        content.dbId = diagram.getDbId();
        content.forNormalDraw = diagram.getForNormalDraw();
        content.hideCompartmentInName = diagram.getHideCompartmentInName();
        content.isDisease = diagram.getIsDisease();

        initialiseObjects(content.diagramObjectMap, diagram.getNodes());
        initialiseObjects(content.diagramObjectMap, diagram.getNotes());
        initialiseObjects(content.diagramObjectMap, diagram.getEdges());
        initialiseObjects(content.diagramObjectMap, diagram.getLinks());
        initialiseObjects(content.diagramObjectMap, diagram.getCompartments());

        //Get normal, diseased components etc.
//        content.normalComponents = getDiagramObjectSet(content.diagramObjectMap, diagram.getNormalComponents());
        content.diseaseComponents = getDiagramObjectSet(content.diagramObjectMap, diagram.getDiseaseComponents());
        content.lofNodes = getDiagramObjectSet(content.diagramObjectMap, diagram.getLofNodes());

        content.minX = diagram.getMinX().doubleValue();
        content.maxX = diagram.getMaxX().doubleValue();
        content.minY = diagram.getMinY().doubleValue();
        content.maxY = diagram.getMaxY().doubleValue();

        return content;
    }

    private static void initialiseObjects(Map<Long, DiagramObject> map, Collection<? extends DiagramObject> objects){
        if(objects==null) return;
        for (DiagramObject object : objects) {
            map.put(object.getId(), object);
        }
    }

    private static Set<DiagramObject> getDiagramObjectSet(Map<Long, DiagramObject> map, Set<Long> list){
        Set<DiagramObject> rtn = new HashSet<>();
        if(list!=null) {
            for (Long id : list) {
                DiagramObject diagramObject = map.get(id);
                if (diagramObject != null) {
                    rtn.add(diagramObject);
                }
            }
        }
        return rtn;
    }

    public static void fillGraphContent(DiagramContent content, Graph graph){
        DatabaseObjectFactory.content = content;

        for (EntityNode node : graph.getNodes()) {
            DatabaseObjectFactory.getOrCreateDatabaseObject(node);
        }
        for (EventNode edge : graph.getEdges()) {
            DatabaseObjectFactory.getOrCreateDatabaseObject(edge);
        }

        for (EntityNode node : graph.getNodes()) {
            GraphObject obj = content.getDatabaseObject(node.getDbId());
            if(obj instanceof GraphPhysicalEntity) {
                GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pe.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pe);
                }

                List<GraphPhysicalEntity> parents = getDatabaseObjects(node.getParents());
                pe.addParent(parents);

                List<GraphPhysicalEntity> children = getDatabaseObjects(node.getChildren());
                pe.addChildren(children);
            }else if(obj instanceof GraphPathway){
                GraphPathway pathway = (GraphPathway) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pathway.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pathway);
                }

                //TODO: Need to keep parents and/or children?
            }
        }

        for (EventNode edge : graph.getEdges()) {
            GraphReactionLikeEvent event = (GraphReactionLikeEvent) content.getDatabaseObject(edge.getDbId());

            DiagramObject diagramObject = DatabaseObjectFactory.content.getDiagramObject(edge.getDiagramId());
            event.addDiagramObject(diagramObject);
            diagramObject.setGraphObject(event);

            List<GraphPhysicalEntity> inputs = getDatabaseObjects(edge.getInputs());
            event.setInputs(inputs);

            List<GraphPhysicalEntity> outputs = getDatabaseObjects(edge.getOutputs());
            event.setOutputs(outputs);

            List<GraphPhysicalEntity> catalysts = getDatabaseObjects(edge.getCatalysts());
            event.setCatalysts(catalysts);

            List<GraphPhysicalEntity> activators = getDatabaseObjects(edge.getActivators());
            event.setActivators(activators);

            List<GraphPhysicalEntity> inhibitors = getDatabaseObjects(edge.getInhibitors());
            event.setInhibitors(inhibitors);

            List<GraphPhysicalEntity> requirements = getDatabaseObjects(edge.getRequirements());
            event.setRequirements(requirements);

            List<GraphReactionLikeEvent> preceding = getDatabaseObjects(edge.getPreceding());
            event.setPrecedingEvents(preceding);

            List<GraphReactionLikeEvent> following = getDatabaseObjects(edge.getFollowing());
            event.setFollowingEvents(following);
        }

        if(graph.getSubpathways()!=null) {
            for (SubpathwayRaw subpathway : graph.getSubpathways()) {
                GraphSubpathway sp = DatabaseObjectFactory.getOrCreateDatabaseObject(subpathway);
                for (Long event : subpathway.getEvents()) {
                    sp.addContainedEvent((GraphReactionLikeEvent) content.getDatabaseObject(event));
                }
            }
        }
    }

    private static List<DiagramObject> getDiagramObjects(List<Long> ids){
        List<DiagramObject> rtn = new ArrayList<>();
        if(ids!=null){
            for (Long id : ids) {
                rtn.add(DatabaseObjectFactory.content.getDiagramObject(id));
            }
        }
        return rtn;
    }

    private static  <T extends GraphObject> List<T> getDatabaseObjects(List<Long> dbIds){
        List<T> rtn = new ArrayList<>();
        if(dbIds!=null) {
            for (Long dbId : dbIds) {
                //noinspection unchecked
                T t = (T) DatabaseObjectFactory.content.getDatabaseObject(dbId);
                if(t!=null) {
                    rtn.add(t);
                }
            }
        }
        return rtn;
    }
}
