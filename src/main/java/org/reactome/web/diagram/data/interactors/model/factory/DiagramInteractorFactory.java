package org.reactome.web.diagram.data.interactors.model.factory;

import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.raw.GraphNode;
import org.reactome.web.diagram.data.interactors.raw.DiagramInteractors;
import org.reactome.web.diagram.data.interactors.raw.EntityInteractor;
import org.reactome.web.diagram.data.interactors.raw.Interactor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.util.MapSet;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramInteractorFactory {

    public static void createInteractors(DiagramContent content, DiagramInteractors diagramInteractors){
        MapSet<String, DiagramObject> map = new MapSet<>(); //TODO: Move this to DiagramContent so it's done only once
        for (GraphObject graphObject : content.getDatabaseObjects()) {
            if(graphObject instanceof GraphNode){
                GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                map.add(pe.getIdentifier(), pe.getDiagramObjects());
                for (DiagramObject diagramObject : pe.getDiagramObjects()) {
                    if(diagramObject instanceof Node){
                        Node node = (Node) diagramObject;
//                        node.getSummaryItems().
                    }
                }
            }
        }


        for (EntityInteractor entity : diagramInteractors.getEntities()) {
            for (Interactor interactor : entity.getInteractors()) {

            }
        }

    }
}
