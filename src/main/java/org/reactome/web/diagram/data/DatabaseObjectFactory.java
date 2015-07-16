package org.reactome.web.diagram.data;

import com.google.gwt.core.client.GWT;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.model.factory.ModelFactoryException;
import org.reactome.web.diagram.data.graph.model.factory.SchemaClass;
import org.reactome.web.diagram.data.graph.raw.*;

/**
 * This factory is created and kept for every diagram in its context. This is meant to keep previous loaded
 * diagrams in memory for quick revisiting (LRU is recommended though)
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DatabaseObjectFactory {
    //LayoutLoader is in charge of update the content when a new
    public static DiagramContent content;

    public static DatabaseObject getOrCreateDatabaseObject(GraphNode node){
        if(node ==null || node.getDbId()==null){
            throw new RuntimeException("It is not possible to create a DatabaseObject for " + node);
        }

        DatabaseObject dbObject = content.getDatabaseObject(node.getDbId());
        if (dbObject != null) return dbObject;

        String auxSchemaClass = node.getSchemaClass();
        SchemaClass schemaClass = SchemaClass.getSchemaClass(auxSchemaClass);

        switch (schemaClass){
            case CANDIDATE_SET:                     dbObject = new CandidateSet((EntityNode) node);                    break;
            case COMPLEX:                           dbObject = new Complex((EntityNode) node);                         break;
            case DEFINED_SET:                       dbObject = new DefinedSet((EntityNode) node);                      break;
            case ENTITY_SET:                        dbObject = new EntitySet((EntityNode) node);                       break;
            case ENTITY_WITH_ACCESSIONED_SEQUENCE:  dbObject = new EntityWithAccessionedSequence((EntityNode) node);   break;
            case GENOME_ENCODED_ENTITY:             dbObject = new GenomeEncodedEntity((EntityNode) node);             break;
            case OPEN_SET:                          dbObject = new OpenSet((EntityNode) node);                         break;
            case OTHER_ENTITY:                      dbObject = new OtherEntity((EntityNode) node);                     break;
            case PATHWAY:                           dbObject = new Pathway((EntityNode) node);                          break;
            case POLYMER:                           dbObject = new Polymer((EntityNode) node);                         break;
            case SIMPLE_ENTITY:                     dbObject = new SimpleEntity((EntityNode) node);                    break;
            case BLACK_BOX_EVENT:                   dbObject = new BlackBoxEvent((EventNode) node);                    break;
            case DEPOLYMERISATION:                  dbObject = new Depolymerisation((EventNode) node);                 break;
            case FAILED_REACTION:                   dbObject = new FailedReaction((EventNode) node);                   break;
            case POLYMERISATION:                    dbObject = new Polymerisation((EventNode) node);                   break;
            case REACTION:                          dbObject = new Reaction((EventNode) node);                         break;
            case GO_CELLULAR_COMPONENT:             dbObject = new GO_CellularComponent((EntityNode) node);            break;
            case COMPARTMENT:                       dbObject = new Compartment((EntityNode) node);                     break;
            case ENTITY_COMPARTMENT:                dbObject = new EntityCompartment((EntityNode) node);               break;
            default:
                String msg = "It is not possible to create a DatabaseObject. " + node;
                GWT.log(msg);
                throw new ModelFactoryException(msg);
        }

        content.cache(dbObject);
        return dbObject;
    }

    public static Subpathway getOrCreateDatabaseObject(SubpathwayRaw subpathway){
        if(subpathway ==null || subpathway.getDbId()==null){
            throw new RuntimeException("It is not possible to create a DatabaseObject for " + subpathway);
        }

        Subpathway rtn = (Subpathway) content.getDatabaseObject(subpathway.getDbId());
        if (rtn != null) return rtn;

        rtn = new Subpathway(subpathway);
        for (Long id : subpathway.getEvents()) {
            DatabaseObject databaseObject = content.getDatabaseObject(id);
            if(databaseObject!=null && databaseObject instanceof ReactionLikeEvent){
                rtn.addContainedEvent((ReactionLikeEvent) databaseObject);
            }
        }

        content.cache(rtn);
        return rtn;
    }
}
