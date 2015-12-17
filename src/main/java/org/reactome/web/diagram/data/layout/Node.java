package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Node extends NodeCommon {

    List<NodeAttachment> getNodeAttachments();

    List<Connector> getConnectors();

    Boolean getTrivial();

    SummaryItem getInteractorsSummary();
}
