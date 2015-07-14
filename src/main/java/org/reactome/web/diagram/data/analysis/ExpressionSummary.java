package org.reactome.web.diagram.data.analysis;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface ExpressionSummary {
    List<String> getColumnNames();
    Double getMin();
    Double getMax();
}
