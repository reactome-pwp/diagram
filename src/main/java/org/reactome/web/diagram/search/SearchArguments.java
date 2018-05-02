package org.reactome.web.diagram.search;

import com.google.gwt.regexp.shared.RegExp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable class that holds the arguments of the specific search.
 * e.g. the query, each of the search terms, the diagramId etc.
 * All query terms are stored in lowercase.
 * <p/>
 * Also this class is responsible for compiling once the regular expression
 * used for highlighting of the results.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchArguments {
    private String query;
    private String diagramStId;
    private String species;
    private Set<String> facets;
    private int facetsScope;

    private List<String> terms = new ArrayList();
    private RegExp highlightingRegExp = null;

    public SearchArguments(String query, String diagramStId, String species, Set<String> facets, int facetsScope) {
        this.query = query.toLowerCase();
        this.diagramStId = diagramStId;
        this.species = species;
        this.facets = facets;
        this.facetsScope = facetsScope;

        if (hasValidQuery()) {
            String[] allTerms = query.toLowerCase().split("  *");
            highlightingRegExp = compileHighlightingExpression(allTerms);
        }
    }

    public boolean hasValidQuery(){
        return query != null && !query.isEmpty();
    }

    public String getQuery() {
        return query;
    }

    public String getDiagramStId() {
        return diagramStId;
    }

    public List<String> getTerms() {
        return terms;
    }

    public int sizeOfTerms() {
        return terms.size();
    }

    public String getSpecies() {
        return species;
    }

    public Set<String> getFacets() {
        return facets;
    }

    public int getFacetsScope() {
        return facetsScope;
    }

    public RegExp getHighlightingExpression() {
        return highlightingRegExp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchArguments that = (SearchArguments) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(diagramStId, that.diagramStId) &&
                Objects.equals(species, that.species) &&
                Objects.equals(facets, that.facets);
    }

    @Override
    public int hashCode() {

        return Objects.hash(query, diagramStId, species, facets);
    }

    @Override
    public String toString() {
        return "SearchArguments{" +
                "query='" + query + '\'' +
                " terms='" + terms.size() + '\'' +
                ", diagramStId='" + diagramStId + '\'' +
                ", species='" + species + '\'' +
                ", facets='" + facets + '\'' +
                '}';
    }

    private RegExp compileHighlightingExpression(String[] allTerms) {
        RegExp highlightingRegExp = null;
        if (allTerms.length != 0) {
            terms = Arrays.asList(allTerms);

            /*
             * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
             *                    be referred later.
             * gi               : global search and case insensitive
             * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
             *                    that is the reference to the first matched group. This means that we want to
             *                    replace it using the exact word that was found.
             */
            String aux = terms.stream()
                              .map(term -> RegExp.quote(term))
                              .collect(Collectors.joining("|", "(", ")"));
            try {
                highlightingRegExp = RegExp.compile(aux, "gi");
            } catch (RuntimeException e) {
                //In case something goes wrong do not highlight anything
                highlightingRegExp = RegExp.compile("/.^/");
            }
        }
        return highlightingRegExp;
    }
}
