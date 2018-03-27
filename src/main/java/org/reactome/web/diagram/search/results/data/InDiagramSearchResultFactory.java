package org.reactome.web.diagram.search.results.data;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.search.results.data.model.Entry;
import org.reactome.web.diagram.search.results.data.model.InDiagramSearchResult;
import org.reactome.web.diagram.search.results.data.model.Occurrences;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InDiagramSearchResultFactory {

    @SuppressWarnings("UnusedDeclaration")
//    @Category(EntryCategory.class)
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<Entry> entry();
        AutoBean<InDiagramSearchResult> inDiagramSearchResult();
        AutoBean<Occurrences> occurrences();

    }

    @SuppressWarnings("Duplicates")
    public static <T> T getSearchObject(Class<T> cls, String json) throws InDiagramSearchException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new InDiagramSearchException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }
}
