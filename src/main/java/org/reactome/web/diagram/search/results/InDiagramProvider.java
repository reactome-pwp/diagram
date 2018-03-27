package org.reactome.web.diagram.search.results;

import org.reactome.web.diagram.search.results.data.InDiagramSearchException;
import org.reactome.web.diagram.search.results.data.InDiagramSearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.InDiagramSearchResult;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.scroller.client.provider.AbstractListAsyncDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InDiagramProvider extends AbstractListAsyncDataProvider<ResultItem> {

    @Override
    protected List<ResultItem> processResult(String body) {
        List<ResultItem> rtn = new ArrayList<>();
        try {
            InDiagramSearchResult result = InDiagramSearchResultFactory.getSearchObject(InDiagramSearchResult.class, body);
            rtn = result.getEntries().stream()
                    .map(ResultItem::new)
                    .collect(Collectors.toList());

        } catch (InDiagramSearchException e) {
            //TODO deal with this error
            Console.error(e.getMessage());
        }
        return rtn;
    }
}
