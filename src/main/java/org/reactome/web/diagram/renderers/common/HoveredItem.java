package org.reactome.web.diagram.renderers.common;

/**
 * Some items draw part of other items (e.g. nodes can also render some connectors that belong to the edges).
 * When those parts belonging to other objects are hovered, this will need to be notified by the object that
 * is drawing (and not the "owner" itself).
 *
 * We also have to take into account that there are inner elements in the drawn objects which we need to keep
 * track of. For example the "summary items" or "expression levels" in the nodes.
 *
 *   ----------------------------
 *   | [X]                  [X] |       [X] -> These are summary items
 *   |       display name       |
 *   | [X]                  [X] |
 *   ----------------------------
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class HoveredItem {

    private Long diagramId;
    private Double expression;
    private SummaryItem summaryItem;

    public HoveredItem(Long diagramId) {
        this.diagramId = diagramId;
    }

    public HoveredItem(Long diagramId, Double expression) {
        this.diagramId = diagramId;
        this.expression = expression;
    }

    public HoveredItem(Long diagramId, SummaryItem summaryItem) {
        this.diagramId = diagramId;
        this.summaryItem = summaryItem;
    }

    public HoveredItem(Long diagramId, Double expression, SummaryItem summaryItem) {
        this.diagramId = diagramId;
        this.expression = expression;
        this.summaryItem = summaryItem;
    }

    public Long getDiagramId() {
        return diagramId;
    }

    public Double getExpression() {
        return expression;
    }

    public SummaryItem getSummaryItem() {
        return summaryItem;
    }
}
