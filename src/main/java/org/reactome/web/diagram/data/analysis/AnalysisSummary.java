package org.reactome.web.diagram.data.analysis;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface AnalysisSummary {

    String getToken();

    String getType();

    Long getSpecies();

    String getFileName();

    boolean isText();

    String getSampleName();

}
