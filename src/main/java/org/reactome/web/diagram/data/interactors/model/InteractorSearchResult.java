package org.reactome.web.diagram.data.interactors.model;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.model.images.InteractorImages;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.search.SearchResultObject;

import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorSearchResult implements Comparable<InteractorSearchResult>, SearchResultObject {

    private String resource;
    private String diagramAcc;
    private RawInteractor rawInteractor;
    private Set<GraphObject> interactsWith;

    private String primary;
    private String secondary;

    public InteractorSearchResult(String resource, String diagramAcc, RawInteractor rawInteractor,  Set<GraphObject> interactsWith) {
        this.resource = resource;
        this.diagramAcc = diagramAcc;
        this.rawInteractor = rawInteractor;
        this.interactsWith = interactsWith;
    }

    public String getResource() {
        return resource;
    }

    public String getDiagramAcc() {
        return diagramAcc;
    }

    public String getAcc() {
        return rawInteractor.getAcc();
    }

    public String getId() {
        return rawInteractor.getId();
    }

    public Set<GraphObject> getInteractsWith() {
        return interactsWith;
    }

    @Override
    public ImageResource getImageResource() {
        return InteractorImages.INSTANCE.interactor();
    }

    @Override
    public String getPrimarySearchDisplay() {
        return primary;
    }

    @Override
    public String getSecondarySearchDisplay() {
        return secondary;
    }

    @Override
    public void setSearchDisplay(String[] searchTerms) {
        primary = rawInteractor.getAcc();
        secondary= rawInteractor.getId();

        if (searchTerms == null || searchTerms.length == 0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");
        String term = sb.toString();
        /**
         * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
         *                    be referred later.
         * gi               : global search and case insensitive
         * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
         *                    that is the reference to the first matched group. This means that we want to
         *                    replace it using the exact word that was found.
         */
        RegExp regExp = RegExp.compile(term, "gi");
        primary = regExp.replace(primary, "<u><strong>$1</strong></u>");
        secondary = regExp.replace(secondary, "<u><strong>$1</strong></u>");
    }

    @Override
    public int compareTo(InteractorSearchResult o) {
        int cmp = rawInteractor.getAcc().compareTo(o.rawInteractor.getAcc());
        if (cmp == 0) return rawInteractor.getId().compareTo(o.rawInteractor.getId());
        return cmp;
    }
}
