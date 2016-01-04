package org.reactome.web.diagram.data.graph.model.factory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public enum SchemaClass {
    ABSTRACT_MODIFIED_RESIDUE("AbstractModifiedResidue"),
    BLACK_BOX_EVENT("BlackBoxEvent"),
    BOOK("Book"),
    CANDIDATE_SET("CandidateSet"),
    CATALYST_ACTIVITY("CatalystActivity"),
    CELL_TYPE("CellType"),
    COMPARTMENT("Compartment"),
    COMPLEX("Complex"),
    COMPLEX_DOMAIN("ComplexDomain"),
    CROSS_LINKED_RESIDUE("CrosslinkedResidue"),
    DATABASE_IDENTIFIER("DatabaseIdentifier"),
    DATABASE_OBJECT("DatabaseObject"),
    DEFINED_SET("DefinedSet"),
    DEPOLYMERISATION("Depolymerisation"),
    DISEASE("Disease"),
    DOMAIN("Domain"),
    ENTITY_COMPARTMENT("EntityCompartment"),
    ENTITY_FUNCTIONAL_STATUS("EntityFunctionalStatus"),
    ENTITY_SET("EntitySet"),
    ENTITY_WITH_ACCESSIONED_SEQUENCE("EntityWithAccessionedSequence", "Protein"),
    EVIDENCE_TYPE("EvidenceType"),
    EXTERNAL_ONTOLOGY("ExternalOntology"),
    FAILED_REACTION("FailedReaction"),
    FIGURE("Figure"),
    FRAGMENT_DELETION_MODIFICATION("FragmentDeletionModification"),
    FRAGMENT_INSERTION_MODIFICATION("FragmentInsertionModification"),
    FRAGMENT_REPLACED_MODIFICATION("FragmentReplacedModification"),
    FRAGMENT_MODIFICATION("FragmentModification"),
    FRONT_PAGE("FrontPage"),
    FUNCTIONAL_STATUS("FunctionalStatus"),
    FUNCTIONAL_STATUS_TYPE("FunctionalStatusType"),
    GENERIC_DOMAIN("GenericDomain"),
    GENETICALLY_MODIFIED_RESIDUE("GeneticallyModifiedResidue"),
    GENOME_ENCODED_ENTITY("GenomeEncodedEntity"),
    GO_BIOLOGICAL_PROCESS("GO_BiologicalProcess"),
    GO_BIOLOGICAL_FUNCTION("GO_MolecularFunction"),
    GO_CELLULAR_COMPONENT("GO_CellularComponent"),
    GROUP_MODIFIED_RESIDUE("GroupModifiedResidue"),
    INSTANCE_EDIT("InstanceEdit"),
    INTER_CHAIN_CROSSLINKED_RESIDUE("InterChainCrosslinkedResidue"),
    INTRA_CHAIN_CROSSLINKED_RESIDUE("IntraChainCrosslinkedResidue"),
    LITERATURE_REFERENCE("LiteratureReference"),
    MODIFIED_RESIDUE("ModifiedResidue"),
    NEGATIVE_REGULATION("NegativeRegulation"),
    OPEN_SET("OpenSet"),
    OTHER_ENTITY("OtherEntity"),
    PATHWAY("Pathway"),
    PERSON("Person"),
    PHYSICAL_ENTITY("PhysicalEntity"),
    POLYMER("Polymer"),
    POLYMERISATION("Polymerisation"),
    POSITIVE_REGULATION("PositiveRegulation"),
    PSI_MOD("PsiMod"),
    PUBLICATION("Publication"),
    REACTION("Reaction"),
    REACTION_LIKE_EVENT("ReactionLikeEvent"),
    REFERENCE_DATABASE("ReferenceDatabase"),
    REFERENCE_DNA_SEQUENCE("ReferenceDNASequence", "Reference DNA sequence"),
    REFERENCE_ENTITY("ReferenceEntity"),
    REFERENCE_GENE_PRODUCT("ReferenceGeneProduct"),
    REFERENCE_GROUP("ReferenceGroup"),
    REFERENCE_ISOFORM("ReferenceIsoform"),
    REFERENCE_MOLECULE("ReferenceMolecule"),
    REFERENCE_RNA_SEQUENCE("ReferenceRNASequence", "Reference RNA sequence"),
    REFERENCE_SEQUENCE("ReferenceSequence"),
    REGULATION("Regulation"),
    REGULATION_TYPE("RegulationType"),
    REPLACED_RESIDUE("ReplacedResidue"),
    REQUIREMENT("Requirement"),
    SEQUENCE_DOMAIN("SequenceDomain"),
    SEQUENCE_ONTOLOGY("SequenceOntology"),
    SIMPLE_ENTITY("SimpleEntity"),
    SPECIES("Species"),
    STABLE_IDENTIFIER("StableIdentifier"),
    SUMMATION("Summation"),
    TAXON("Taxon"),
    TRANSLATIONAL_MODIFICATION("TranslationalModification"),
    URL("URL");

    public final String schemaClass;
    public final String name;

    SchemaClass(String schemaClass) {
        this(schemaClass, getName(schemaClass));
    }

    SchemaClass(String schemaClass, String name){
        this.schemaClass = schemaClass;
        this.name = name;
    }

    public static SchemaClass getSchemaClass(String schemaClass){
        for (SchemaClass sc : values()) {
            if(sc.schemaClass.equals(schemaClass)) return sc;
        }
        return DATABASE_OBJECT;
    }

    private static String getName(String schemaClass) {
        StringBuilder sb = new StringBuilder();
        //Following code does the same than the commented for underneath
        // The problem with the split(regex) is that the shown one is not working in javascript
        // so is necessary to do that in using another approach
        for(int pos = 0; pos < schemaClass.length(); ++pos){
            String c = String.valueOf(schemaClass.charAt(pos));// schemaClass.substring(pos, pos);
            //noinspection NonJREEmulationClassesInClientCode
            if(c.matches("[A-Z]"))
                sb.append(" ");
            sb.append(c);
        }
        //READ THE PREVIOUS COMMENT before deleting the next commented code
        /*for (String word : schemaClass.split("(?<!^)(?=[A-Z])")) {
            sb.append(word);
            sb.append(" ");
        }*/
        return sb.toString().trim();
    }
}
