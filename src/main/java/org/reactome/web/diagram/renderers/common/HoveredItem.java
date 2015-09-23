package org.reactome.web.diagram.renderers.common;

import org.reactome.web.diagram.data.layout.NodeAttachment;

/**
 * Some items draw part of other items (e.g. nodes can also render some connectors that belong to the edges).
 * When those parts belonging to other objects are hovered, this will need to be notified by the object that
 * is drawing (and not the "owner" itself).
 *
 * We also have to take into account that there are inner elements in the drawn objects which we need to keep
 * track of. For example the "summary items" or "expression levels" in the nodes.
 *
 *   --------------[A]-[A]-------       [A] -> These are node attachments
 *   | [X]                  [X] |       [X] -> These are summary items
 *   |       display name       |
 *   | [X]                  [X] |
 *   ----------------------------
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class HoveredItem {

    private Long diagramId;
    private NodeAttachment attachment;
    private SummaryItem summaryItem;

    public HoveredItem(Long diagramId) {
        this.diagramId = diagramId;
    }

    public HoveredItem(Long diagramId, NodeAttachment attachment) {
        this.diagramId = diagramId;
        this.attachment = attachment;
    }

    public HoveredItem(Long diagramId, SummaryItem summaryItem) {
        this.diagramId = diagramId;
        this.summaryItem = summaryItem;
    }

    public Long getDiagramId() {
        return diagramId;
    }

    public NodeAttachment getAttachment() {
        return attachment;
    }

    public SummaryItem getSummaryItem() {
        return summaryItem;
    }

    @Override
    public String toString() {
        return "HoveredItem{" +
                "diagramId=" + diagramId +
                (attachment == null ? "" :  ", attachment=" + attachment.getReactomeId()) +
                (summaryItem == null ? "" : ", summaryItem=" + summaryItem) +
                '}';
    }
}
