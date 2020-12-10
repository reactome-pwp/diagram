package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Node extends NodeCommon {

    List<NodeAttachment> getNodeAttachments();

    List<Connector> getConnectors();

    Boolean getTrivial();
    
    SummaryItem getInteractorsSummary();
    
    void setInteractorsSummary(SummaryItem interactorsSummary);

    //Keeping a pointer the the cached object improves performance avoiding searching for it every time it gets toggled

    InteractorsSummary getDiagramEntityInteractorsSummary();

    void setDiagramEntityInteractorsSummary(InteractorsSummary interactorsSummary);
    
    //next to methods added so other decorator can be added based on SummaryItem interface
    List<SummaryItem> getOtherDecoratorsList();
    
    void setOtherDecoratorsList(List<SummaryItem> otherDecoratorsList);
}
