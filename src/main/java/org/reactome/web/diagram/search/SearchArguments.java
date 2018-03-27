package org.reactome.web.diagram.search;

import java.util.Objects;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchArguments {
    private String term;
    private String diagramStId;

    public SearchArguments(String term, String diagramStId) {
        this.term = term;
        this.diagramStId = diagramStId;
    }

    public boolean hasValidTerm(){
        return term != null && !term.isEmpty();
    }

    public String getTerm() {
        return term;
    }

    public String getDiagramStId() {
        return diagramStId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchArguments that = (SearchArguments) o;
        return Objects.equals(term, that.term) &&
                Objects.equals(diagramStId, that.diagramStId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, diagramStId);
    }

    @Override
    public String toString() {
        return "SearchArguments{" +
                "term='" + term + '\'' +
                ", diagramStId='" + diagramStId + '\'' +
                '}';
    }
}
