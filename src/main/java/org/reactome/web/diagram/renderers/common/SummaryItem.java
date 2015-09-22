package org.reactome.web.diagram.renderers.common;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public enum SummaryItem {
    TL("None", "N/A"),
    TR("Interactors", "The number of interactors found for this entity"),
    BR("None", "N/A"),
    BL("None", "N/A");

    private String feature;
    private String description;

    SummaryItem(String feature, String description) {
        this.feature = feature;
        this.description = description;
    }
}
