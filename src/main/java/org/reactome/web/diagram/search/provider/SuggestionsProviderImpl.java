package org.reactome.web.diagram.search.provider;

import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SuggestionsProviderImpl implements SuggestionsProvider<GraphObject> {

    private DiagramContent content;

    public SuggestionsProviderImpl(DiagramContent content) {
        this.content = content;
    }

    @Override
    public List<GraphObject> getSuggestions(String input) {
        List<GraphObject> rtn = new ArrayList<>();
        if (content == null || input == null || input.isEmpty()) return rtn;

        String[] inputs = input.split("  *");
        if(inputs.length==0) return rtn;

        String term = inputs[0].toLowerCase();
        for (GraphObject obj : content.getDatabaseObjects()) {
            obj.clearSearchDisplayValue(); //clears the result of previous searches
            if (obj.getDisplayName().toLowerCase().contains(term)) {
                rtn.add(obj);
            }
        }

        // improvement for extra terms filtering
        if (inputs.length > 1) {
            for (int i = 1; i < inputs.length; i++) {
                term = inputs[i].toLowerCase();
                List<GraphObject> aux = new ArrayList<>();
                for (GraphObject obj : rtn) {
                    if (obj.getDisplayName().toLowerCase().contains(term)) {
                        aux.add(obj);
                    }
                }
                rtn = aux;
            }
        }

        Collections.sort(rtn);
        for (GraphObject object : rtn) {
            object.setSearchDisplay(inputs);
        }
        return rtn;
    }
}
