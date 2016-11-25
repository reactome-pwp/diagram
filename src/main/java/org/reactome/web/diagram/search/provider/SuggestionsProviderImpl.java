package org.reactome.web.diagram.search.provider;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.search.SearchResultObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SuggestionsProviderImpl implements SuggestionsProvider<SearchResultObject> {

    private Context context;

    public SuggestionsProviderImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<SearchResultObject> getSuggestions(String input) {
        List<SearchResultObject> rtn = new ArrayList<>();
        if (context == null || input == null || input.isEmpty()) return rtn;

        String[] inputs = input.split("  *");
        if (inputs.length == 0) return rtn;

        String term = inputs[0].toLowerCase();
        for (GraphObject obj : context.getContent().getDatabaseObjects()) {
            obj.clearSearchDisplayValue(); //clears the result of previous searches
            if (isTermInObject(obj, term)) {
                rtn.add(obj);
            }
        }
        for (InteractorSearchResult obj : context.getInteractors().getInteractorSearchResult(LoaderManager.INTERACTORS_RESOURCE, context.getContent())) {
            if(isTermInObject(obj, term)) {
                rtn.add(obj);
            }
        }

        // improvement for extra terms filtering
        if (inputs.length > 1) {
            for (int i = 1; i < inputs.length; i++) {
                term = inputs[i].toLowerCase();
                List<SearchResultObject> aux = new ArrayList<>();
                for (SearchResultObject obj : rtn) {
                    if (isTermInObject(obj, term)) {
                        aux.add(obj);
                    }
                }
                rtn = aux;
            }
        }

        // We first give priority to the diagram objects and then the interactors
        // both GraphObject and InteractorSearchResult implements Comparable so
        // they can use the compareTo with one each other
        Collections.sort(rtn, new Comparator<SearchResultObject>() {
            @Override
            public int compare(SearchResultObject o1, SearchResultObject o2) {
                if(o1 instanceof GraphObject){
                    if(o2 instanceof GraphObject) {
                        return ((GraphObject) o1).compareTo((GraphObject) o2);
                    } else {
                        return -1;
                    }
                } else {
                    if(o2 instanceof GraphObject) {
                        return -2;
                    } else {
                        return ((InteractorSearchResult) o1).compareTo((InteractorSearchResult) o2);
                    }
                }
            }
        });

        for (SearchResultObject object : rtn) {
            object.setSearchDisplay(inputs);
        }
        return rtn;
    }

    private boolean isTermInObject(SearchResultObject searchResultObject, String term) {
        if(searchResultObject instanceof GraphObject) {
            GraphObject obj = (GraphObject) searchResultObject;
            if (obj.getDisplayName().toLowerCase().contains(term)) {
                return true;
            }
            if (obj instanceof GraphPhysicalEntity) {
                GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                if (pe.getIdentifier() != null && pe.getIdentifier().toLowerCase().contains(term)) {
                    return true;
                }
                if (pe.getGeneNames() != null) {
                    for (String geneName : pe.getGeneNames()) {
                        if (geneName.toLowerCase().contains(term)) {
                            return true;
                        }
                    }
                }
            }
            return obj.getStId().toLowerCase().contains(term);
        } else if (searchResultObject instanceof InteractorSearchResult){
            InteractorSearchResult obj = (InteractorSearchResult) searchResultObject;
            return obj.containsTerm(term);
        }
        return false;
    }
}
