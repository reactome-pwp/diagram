package org.reactome.web.diagram.data.interactors.model;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.images.InteractorImages;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorSearchResult implements Comparable<InteractorSearchResult>, SearchResultObject {

    private OverlayResource resource;
    private String accession;
    private String alias;
    private int evidences = 0;
    private Map<Long, RawInteractor> interaction;
    private MapSet<Long, GraphObject> interactsWith;

    private String primary;
    private String secondary;

    public InteractorSearchResult(OverlayResource resource, String accession, String alias) {
        this.resource = resource;
        this.accession = accession;
        this.alias = alias;
        this.interactsWith = new MapSet<>();
        this.interaction = new HashMap<>();
    }

    public void addInteractsWith(Long interactionId, Set<GraphObject> interactsWith){
        this.interactsWith.add(interactionId, interactsWith);
    }

    public void addInteraction(RawInteractor rawInteractor){
        this.interaction.put(rawInteractor.getId(), rawInteractor);
        if(rawInteractor.getEvidences()!=null) {
            evidences += rawInteractor.getEvidences();
        }
    }

    public boolean containsTerm(String term){
        String alias = this.alias != null ? this.alias : ""; //Alias can be null
        return alias.toLowerCase().contains(term) || accession.toLowerCase().contains(term);
    }

    public OverlayResource getResource() {
        return resource;
    }

    public String getAccession() {
        return accession;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return alias !=null ? alias : accession;
    }

    public Integer getEvidences(Long interactionId) {
        RawInteractor rawInteractor = interaction.get(interactionId);
        if(rawInteractor!=null && rawInteractor.getEvidences() != null) {
            return rawInteractor.getEvidences();
        } else {
            return 0;
        }
    }

    public Double getInteractionScore(Long interactionId) {
        RawInteractor rawInteractor = interaction.get(interactionId);
        return rawInteractor!=null ? rawInteractor.getScore() : null;
    }

    public MapSet<Long, GraphObject> getInteractsWith() {
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
    public SchemaClass getSchemaClass() {
        return SchemaClass.getSchemaClass("Interactor");
    }

    @Override
    public void setSearchDisplay(String[] searchTerms) {
        if (alias != null) {
            primary = alias;
            secondary = accession;
        } else {
            primary = accession;
        }
        // Adding the number of evidences in the second line of the suggestion
        String evidenceStr = evidences == 0 ? "" : evidences == 1 ? evidences + " evidence" : evidences + " pieces of evidence";
        secondary += ", " + evidenceStr;

        if (searchTerms == null || searchTerms.length == 0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");
        String term = sb.toString();
        /*
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
        return accession.compareTo(o.accession);
    }


}
