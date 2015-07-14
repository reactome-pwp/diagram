package org.reactome.web.diagram.data;

import com.google.gwt.core.client.GWT;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.model.factory.ModelFactoryException;
import org.reactome.web.diagram.data.graph.model.factory.SchemaClass;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.GraphNode;

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

    /*
    public static DatabaseObject getOrCreateDatabaseObject(JSONObject jsonObject) {
        DatabaseObject dbObject;
        if(jsonObject.containsKey("dbId")){
            Long id = FactoryUtils.getLongValue(jsonObject, "dbId");
            dbObject = content.getDatabaseObject(id);
            if(dbObject!=null) return dbObject;
        }

        SchemaClass schemaClass = FactoryUtils.getSchemaClass(jsonObject);

        if(schemaClass==null){
            String msg = "WRONG SCHEMA CLASS. Schema class is empty for " + jsonObject.toString();
            throw new ModelFactoryException(msg);
        }

        switch (schemaClass){
            //case ABSTRACT_MODIFIED_RESIDUE:  //NOT USED HERE
            case BLACK_BOX_EVENT:                       dbObject = new BlackBoxEvent(jsonObject);                       break;
//            case BOOK:                                  dbObject = new Book(jsonObject);                                break;
            case CANDIDATE_SET:                         dbObject = new CandidateSet(jsonObject);                        break;
//            case CATALYST_ACTIVITY:                     dbObject = new CatalystActivity(jsonObject);                    break;
//            case CELL_TYPE:                             dbObject = new CellType(jsonObject);                            break;
            case COMPARTMENT:                           dbObject = new Compartment(jsonObject);                         break;
            case COMPLEX:                               dbObject = new Complex(jsonObject);                             break;
//            case COMPLEX_DOMAIN:                        dbObject = new ComplexDomain(jsonObject);                       break;
            //case CROSS_LINKED_RESIDUE: //NOT USED HERE
//            case DATABASE_IDENTIFIER:                   dbObject = new DatabaseIdentifier(jsonObject);                  break;
            //case DATABASE_OBJECT: //NOT USED HERE
            case DEFINED_SET:                           dbObject = new DefinedSet(jsonObject);                          break;
            case DEPOLYMERISATION:                      dbObject = new Depolymerisation(jsonObject);                    break;
//            case DISEASE:                               dbObject = new Disease(jsonObject);                             break;
            //case DOMAIN:  //NOT USED HERE
            case ENTITY_COMPARTMENT:                    dbObject = new EntityCompartment(jsonObject);                   break;
//            case ENTITY_FUNCTIONAL_STATUS:              dbObject = new EntityFunctionalStatus(jsonObject);              break;
            case ENTITY_SET:                            dbObject = new EntitySet(jsonObject);                           break;
            case ENTITY_WITH_ACCESSIONED_SEQUENCE:      dbObject = new EntityWithAccessionedSequence(jsonObject);       break;
//            case EVIDENCE_TYPE:                         dbObject = new EvidenceType(jsonObject);                        break;
            //case EXTERNAL_ONTOLOGY:   //NOT USED HERE
            case FAILED_REACTION:                       dbObject = new FailedReaction(jsonObject);                      break;
//            case FIGURE:                                dbObject = new Figure(jsonObject);                              break;
//            case FRAGMENT_DELETION_MODIFICATION:        dbObject = new FragmentDeletionModification(jsonObject);        break;
//            case FRAGMENT_INSERTION_MODIFICATION:       dbObject = new FragmentInsertionModification(jsonObject);       break;
//            case FRAGMENT_REPLACED_MODIFICATION:        dbObject = new FragmentReplacedModification(jsonObject);        break;
            //case FRAGMENT_MODIFICATION:  //NOT USED HERE
//            case FRONT_PAGE:                            dbObject = new FrontPage(jsonObject);                           break;
//            case FUNCTIONAL_STATUS:                     dbObject = new FunctionalStatus(jsonObject);                    break;
//            case FUNCTIONAL_STATUS_TYPE:                dbObject = new FunctionalStatusType(jsonObject);                break;
//            case GENERIC_DOMAIN:                        dbObject = new GenericDomain(jsonObject);                       break;
            //case GENETICALLY_MODIFIED_RESIDUE:  //NOT USED HERE
            case GENOME_ENCODED_ENTITY:                 dbObject = new GenomeEncodedEntity(jsonObject);                 break;
//            case GO_BIOLOGICAL_PROCESS:                 dbObject = new GO_BiologicalProcess(jsonObject);                break;
//            case GO_BIOLOGICAL_FUNCTION:                dbObject = new GO_MolecularFunction(jsonObject);                break;
            case GO_CELLULAR_COMPONENT:                 dbObject = new GO_CellularComponent(jsonObject);                break;
//            case GROUP_MODIFIED_RESIDUE:                dbObject = new GroupModifiedResidue(jsonObject);                break;
//            case INSTANCE_EDIT:                         dbObject = new InstanceEdit(jsonObject);                        break;
//            case INTER_CHAIN_CROSSLINKED_RESIDUE:       dbObject = new InterChainCrosslinkedResidue(jsonObject);        break;
//            case INTRA_CHAIN_CROSSLINKED_RESIDUE:       dbObject = new IntraChainCrosslinkedResidue(jsonObject);        break;
//            case LITERATURE_REFERENCE:                  dbObject = new LiteratureReference(jsonObject);                 break;
//            case MODIFIED_RESIDUE:                      dbObject = new ModifiedResidue(jsonObject);                     break;
//            case NEGATIVE_REGULATION:                   dbObject = new NegativeRegulation(jsonObject);                  break;
            case OPEN_SET:                              dbObject = new OpenSet(jsonObject);                             break;
            case OTHER_ENTITY:                          dbObject = new OtherEntity(jsonObject);                         break;
            case PATHWAY:                               dbObject = new Pathway(jsonObject);                             break;
//            case PERSON:                                dbObject = new Person(jsonObject);                              break;
            //case PHYSICAL_ENTITY: //NOT USED HERE
            case POLYMER:                               dbObject = new Polymer(jsonObject);                             break;
            case POLYMERISATION:                        dbObject = new Polymerisation(jsonObject);                      break;
//            case POSITIVE_REGULATION:                   dbObject = new PositiveRegulation(jsonObject);                  break;
//            case PSI_MOD:                               dbObject = new PsiMod(jsonObject);                              break;
            //case PUBLICATION: //NOT USED HERE
            case REACTION:                              dbObject = new Reaction(jsonObject);                            break;
            //case REACTION_LIKE_EVENT: //NOT USED HERE
//            case REFERENCE_DATABASE:                    dbObject = new ReferenceDatabase(jsonObject);                   break;
//            case REFERENCE_DNA_SEQUENCE:                dbObject = new ReferenceDNASequence(jsonObject);                break;
            //case REFERENCE_ENTITY:  //NOT USED HERE
//            case REFERENCE_GENE_PRODUCT:                dbObject = new ReferenceGeneProduct(jsonObject);                break;
//            case REFERENCE_GROUP:                       dbObject = new ReferenceGroup(jsonObject);                      break;
//            case REFERENCE_ISOFORM:                     dbObject = new ReferenceIsoform(jsonObject);                    break;
//            case REFERENCE_MOLECULE:                    dbObject = new ReferenceMolecule(jsonObject);                   break;
//            case REFERENCE_RNA_SEQUENCE:                dbObject = new ReferenceRNASequence(jsonObject);                break;
            //case REFERENCE_SEQUENCE:  //NOT USED HERE
//            case REGULATION:                            dbObject = new Regulation(jsonObject);                          break;
//            case REGULATION_TYPE:                       dbObject = new RegulationType(jsonObject);                      break;
//            case REPLACED_RESIDUE:                      dbObject = new ReplacedResidue(jsonObject);                     break;
//            case REQUIREMENT:                           dbObject = new Requirement(jsonObject);                         break;
//            case SEQUENCE_DOMAIN:                       dbObject = new SequenceDomain(jsonObject);                      break;
//            case SEQUENCE_ONTOLOGY:                     dbObject = new SequenceOntology(jsonObject);                    break;
            case SIMPLE_ENTITY:                         dbObject = new SimpleEntity(jsonObject);                        break;
//            case SPECIES:                               dbObject = new Species(jsonObject);                             break;
//            case STABLE_IDENTIFIER:                     dbObject = new StableIdentifier(jsonObject);                    break;
//            case SUMMATION:                             dbObject = new Summation(jsonObject);                           break;
//            case TAXON:                                 dbObject = new Taxon(jsonObject);                               break;
            //case TRANSLATIONAL_MODIFICATION:   //NOT USED HERE
//            case URL:                                   dbObject = new Url(jsonObject);                                 break;
            default:
                String msg = "[Model Factory] -> Was impossible to instantiate " + jsonObject.toString();
                GWT.log(msg);
                throw new ModelFactoryException(msg);
        }

        content.cache(dbObject);
        return dbObject;
    }
    */
}
