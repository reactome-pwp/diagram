package org.reactome.web.diagram.data.analysis;

import java.util.List;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface EntityStatistics extends Statistics {

    Double getpValue();

    Double getFdr();

    List<Double> getExp();
}
