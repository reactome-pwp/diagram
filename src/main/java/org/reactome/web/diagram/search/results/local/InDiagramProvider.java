package org.reactome.web.diagram.search.results.local;

import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.results.ResultItem;
import org.reactome.web.diagram.search.results.data.DiagramSearchException;
import org.reactome.web.diagram.search.results.data.DiagramSearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.DiagramSearchResult;
import org.reactome.web.diagram.search.results.data.model.SearchError;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.scroller.client.provider.AbstractListAsyncDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.reactome.web.scroller.client.util.Placeholder.ROWS;
import static org.reactome.web.scroller.client.util.Placeholder.START;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("All")
public class InDiagramProvider extends AbstractListAsyncDataProvider<SearchResultObject> {

    private SearchArguments args;
    private StringBuilder stringBuilder;

    private Consumer<DiagramSearchResult> resultConsumer;

    public InDiagramProvider() {
        stringBuilder = new StringBuilder();
    }

    /**
     * Use this constructor in case you need to consume
     * the result in another corner
     */
    public InDiagramProvider(Consumer<DiagramSearchResult> resultConsumer) {
        stringBuilder = new StringBuilder();
        this.resultConsumer = resultConsumer;
    }

    @Override
    protected List<SearchResultObject> processResult(String body) {
        List<SearchResultObject> rtn = new ArrayList<>();
        try {
            DiagramSearchResult result = DiagramSearchResultFactory.getSearchObject(DiagramSearchResult.class, body);
            rtn = result.getEntries().stream()
                    .map(ResultItem::new)
                    .peek(e -> e.setSearchDisplay(args.getHighlightingExpression()))
                    .collect(Collectors.toList());

            if(resultConsumer != null) {
                resultConsumer.accept(result);
            }

        } catch (DiagramSearchException e) {
            //TODO deal with this error
            Console.error(e.getMessage());
        }
        return rtn;
    }

    @Override
    protected String processError(Response response) {
        String rtn = response.getStatusText();
        try {
            SearchError error = DiagramSearchResultFactory.getSearchObject(SearchError.class, response.getText());
            rtn = error.getReason();
        } catch (DiagramSearchException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public void setSearchArguments(SearchArguments args, Set<String> selectedFacets, String baseUrl) {
        this.args = args;
        String types = selectedFacets.stream()
                .map(facet -> facet = "&types=" + facet)
                .collect(Collectors.joining());

        stringBuilder.setLength(0);
        super.setURL(
                stringBuilder
                .append(baseUrl)
                .append(args.getDiagramStId())
                .append("?query=")
                .append(URL.encode(args.getQuery()))
                .append(types)
                .append("&")
                .append(START.getUrlValue())
                .append("&")
                .append(ROWS.getUrlValue())
                .toString()
        );
    }
}
