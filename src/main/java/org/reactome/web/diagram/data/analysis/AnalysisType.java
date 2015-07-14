package org.reactome.web.diagram.data.analysis;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public enum AnalysisType {

    NONE,
    SPECIES_COMPARISON,
    OVERREPRESENTATION,
    EXPRESSION;

    public static AnalysisType getType(String type){
        if(type==null) return  null;
        for (AnalysisType t : values()) {
            if(t.toString().toLowerCase().equals(type.toLowerCase())){
                return t;
            }
        }
        return NONE;
    }
}
