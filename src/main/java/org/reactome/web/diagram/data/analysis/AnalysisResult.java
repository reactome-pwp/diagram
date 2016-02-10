package org.reactome.web.diagram.data.analysis;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface AnalysisResult {

    AnalysisSummary getSummary();

    Integer getPathwaysFound();

    Integer getIdentifiersNotFound();

    List<PathwaySummary> getPathways();

    List<ResourceSummary> getResourceSummary();

    ExpressionSummary getExpression();

    List<String> getWarnings();
}
