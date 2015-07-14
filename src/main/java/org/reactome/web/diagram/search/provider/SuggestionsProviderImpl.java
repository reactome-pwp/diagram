package org.reactome.web.diagram.search.provider;

import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SuggestionsProviderImpl implements SuggestionsProvider<DatabaseObject> {

    private DiagramContent content;

    public SuggestionsProviderImpl(DiagramContent content) {
        this.content = content;
    }

    @Override
    public List<DatabaseObject> getSuggestions(String input) {
        List<DatabaseObject> rtn = new ArrayList<DatabaseObject>();
        if (content == null || input == null || input.isEmpty()) return rtn;

        String[] inputs = input.split("  *");
        if(inputs.length==0) return rtn;

        String term = inputs[0].toLowerCase();
        for (DatabaseObject obj : content.getDatabaseObjects()) {
            obj.clearSearchDisplayValue(); //clears the result of previous searches
            if (obj.getDisplayName().toLowerCase().contains(term)) {
                rtn.add(obj);
            }
        }

        // improvement for extra terms filtering
        if (inputs.length > 1) {
            for (int i = 1; i < inputs.length; i++) {
                term = inputs[i].toLowerCase();
                List<DatabaseObject> aux = new ArrayList<DatabaseObject>();
                for (DatabaseObject obj : rtn) {
                    if (obj.getDisplayName().toLowerCase().contains(term)) {
                        aux.add(obj);
                    }
                }
                rtn = aux;
            }
        }

        Collections.sort(rtn);
        for (DatabaseObject object : rtn) {
            object.setSearchDisplay(inputs);
        }
        return rtn;
    }
}
