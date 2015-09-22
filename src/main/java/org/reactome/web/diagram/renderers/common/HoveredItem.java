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

    private Long hovered;
    private Double expression;
    private SummaryItem summaryItem;

    public HoveredItem(Long hovered) {
        this.hovered = hovered;
    }

    public HoveredItem(Long hovered, Double expression) {
        this.hovered = hovered;
        this.expression = expression;
    }

    public HoveredItem(Long hovered, SummaryItem summaryItem) {
        this.hovered = hovered;
        this.summaryItem = summaryItem;
    }

    public HoveredItem(Long hovered, Double expression, SummaryItem summaryItem) {
        this.hovered = hovered;
        this.expression = expression;
        this.summaryItem = summaryItem;
    }

    public Long getHovered() {
        return hovered;
    }

    public Double getExpression() {
        return expression;
    }

    public SummaryItem getSummaryItem() {
        return summaryItem;
    }
}
