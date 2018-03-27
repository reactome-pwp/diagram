package org.reactome.web.diagram.search.results;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.view.client.ProvidesKey;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.results.data.model.Entry;
import org.reactome.web.diagram.util.SearchResultImageMapper;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultItem implements SearchResultObject, Entry {

    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<ResultItem> KEY_PROVIDER = new ProvidesKey<ResultItem>() {
        @Override
        public Object getKey(ResultItem item) {
            return item == null ? null : item.getId();
        }
    };

    private String stId;
    private String dbId;
    private String name;
    private String exactType;
    private List<String> compartmentNames;
    private String compartments = "-";

    private SearchResultImageMapper.ImageContainer imageContainer;
    private boolean isFlagged;

    public ResultItem(Entry entry) {
        stId = entry.getStId();
        dbId = entry.getId();
        name = entry.getName();
        exactType = entry.getExactType();
        compartmentNames = entry.getCompartmentNames();
        setCompartments(compartmentNames);

        imageContainer = SearchResultImageMapper.getImage(exactType);
    }

    @Override
    public String getStId() {
        return stId;
    }

    @Override
    public String getId() {
        return dbId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExactType() {
        return exactType;
    }

    @Override
    public List<String> getCompartmentNames() {
        return compartmentNames;
    }

    @Override
    public ImageResource getImageResource() {
        return imageContainer.getImageResource();
    }

    @Override
    public String getPrimarySearchDisplay() {
        return name;
    }

    @Override
    public String getSecondarySearchDisplay() {
        return stId;
    }

    @Override
    public String getTertiarySearchDisplay() {
        return compartments;
    }

    @Override
    public void setSearchDisplay(String[] searchTerms) {

    }

//    public boolean isFlagged() {
//        return isFlagged;
//    }
//
//    public void setFlagged(boolean flagged) {
//        isFlagged = flagged;
//    }

    @Override
    public SchemaClass getSchemaClass() {
        return SchemaClass.getSchemaClass(exactType);
    }

    private void setCompartments(List<String> compartmentNames) {
        if (compartmentNames != null && !compartmentNames.isEmpty()) {
            compartments = compartmentNames.stream()
                                    .map(ResultItem::capitalize)
                                    .collect(Collectors.joining(", "));
        }
    }

    private static String capitalize(final String input) {
        if (input == null || (input.length()) == 0) {
            return input;
        }

        final String firstChar = input.substring(0,1);
        return firstChar.toUpperCase() + input.substring(1);
    }
}
