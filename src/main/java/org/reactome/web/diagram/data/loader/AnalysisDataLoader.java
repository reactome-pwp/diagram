package org.reactome.web.diagram.data.loader;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.*;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.analysis.*;
import org.reactome.web.diagram.data.analysis.factory.AnalysisModelException;
import org.reactome.web.diagram.data.analysis.factory.AnalysisModelFactory;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.events.AnalysisResultErrorEvent;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;
import org.reactome.web.diagram.util.Console;

import java.util.List;
import java.util.Set;

/**
 * This class is in charge of providing the analysis result for a given token, resource and pathway.
 * For the pair (token, resource) there is a analysisSummary associated that ONLY needs to be retrieved
 * when these change (either one, the other or both).
 *
 * Related to pathways, please note that there are two components to take into account: (1) the hit
 * entities in the pathway diagram and (2) the entities hit in the encapsulated pathways.
 *
 * It is right to think that the hit entities in the encapsulated pathways are retrieved with the first
 * query but since the deconstruction of the encapsulated pathways (process nodes) is not contained in
 * the graph original graph (and this is done in purpose to optimise memory usage) the easiest way of
 * knowing the percentage of hit elements in this process nodes is querying the analysis service for the
 * pathway summary.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisDataLoader implements AnalysisLoaderHandler {
    private static AnalysisDataLoader analysisDataLoader;
    private final static String PREFIX = "/AnalysisService/token/";

    private EventBus eventBus;

    private AnalysisStatus analysisStatus;
    private DiagramContent diagramContent;
    private AnalysisSummary analysisSummary;
    private ExpressionSummary expressionSummary;

    private PathwayIdentifiers identifiers;

    AnalysisDataLoader(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void initialise(EventBus eventBus) {
        if (analysisDataLoader != null) {
            throw new RuntimeException("Analysis Data Loader has already been initialised. " +
                    "Only one initialisation is permitted per Diagram Viewer instance.");
        }
        analysisDataLoader = new AnalysisDataLoader(eventBus);
    }

    public static AnalysisDataLoader get() {
        if (analysisDataLoader == null) {
            throw new RuntimeException("Analysis Data Loader has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return analysisDataLoader;
    }

    public void loadAnalysisResult(AnalysisStatus analysisStatus, DiagramContent diagramContent){
        this.eventBus.fireEventFromSource(new AnalysisResultRequestedEvent(diagramContent.getDbId()), this);
        this.diagramContent = diagramContent;
        if(analysisStatus!=null && !analysisStatus.equals(this.analysisStatus)){
            this.analysisSummary = null;
            this.expressionSummary = null;
            this.analysisStatus = analysisStatus;
            new ResultSummaryLoader(this, analysisStatus);
        }else{
            onResultSummaryLoaded(this.analysisSummary, this.expressionSummary, 0);
        }
    }

    @Override
    public void onResultSummaryLoaded(AnalysisSummary summary, ExpressionSummary expressionSummary, long time) {
        this.analysisSummary = summary;
        this.expressionSummary = expressionSummary;
        if(diagramContent.containsOnlyEncapsultedPathways()){
            this.identifiers = null;
            new PathwaySummariesLoader(this, this.diagramContent.getEncapsulatedPathways(), this.analysisStatus);
        }else{
            new PathwayIdentifiersLoader(this, this.diagramContent.getDbId(), this.analysisStatus);
        }
    }

    @Override
    public void onResultSummaryNotFound(long time) {
        //TODO: There are not results for the provided token I am afraid
    }

    @Override
    public void onResultSummaryError() {

    }

    @Override
    public void onPathwayIdentifiersLoaded(PathwayIdentifiers identifiers, long time) {
        this.identifiers = identifiers;
        this.loadPathwaySummaries(time);
    }

    @Override
    public void onPathwayIdentifiersNotFound(long time) {
        this.identifiers = null;
        this.loadPathwaySummaries(time);
    }

    private void loadPathwaySummaries(long time){
        if(this.diagramContent.containsEncapsultedPathways()){
            new PathwaySummariesLoader(this, this.diagramContent.getEncapsulatedPathways(), this.analysisStatus);
        }else {
            this.eventBus.fireEventFromSource(new AnalysisResultLoadedEvent(analysisSummary, expressionSummary, identifiers, null, time), this);
        }
    }

    @Override
    public void onPathwayIdentifiersError() {
        //TODO
    }

    @Override
    public void onPathwaySummariesLoaded(List<PathwaySummary> pathwaySummaries, long time) {
        this.eventBus.fireEventFromSource(new AnalysisResultLoadedEvent(analysisSummary, expressionSummary, identifiers, pathwaySummaries, time), this);
    }

    @Override
    public void onPathwaySummariesNotFound(long time) {
        this.eventBus.fireEventFromSource(new AnalysisResultLoadedEvent(analysisSummary,  expressionSummary, identifiers, null, time), this);
    }

    @Override
    public void onPathwaySummariesError() {

    }

    class ResultSummaryLoader implements RequestCallback {
        long start = System.currentTimeMillis();
        AnalysisLoaderHandler handler;
        Request request;

        public ResultSummaryLoader(AnalysisLoaderHandler handler, AnalysisStatus analysisStatus) {
            this.handler = handler;
            String url = PREFIX + analysisStatus.getToken() + "?resource=" + analysisStatus.getResource() + "&pageSize=0&page=1";
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
            try {
                this.request = requestBuilder.sendRequest(null, this);
            } catch (RequestException e) {
                e.printStackTrace();
                handler.onResultSummaryError();
            }
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            long time;
            switch (response.getStatusCode()) {
                case Response.SC_OK:
                    try {
                        AnalysisResult result = AnalysisModelFactory.getModelObject(AnalysisResult.class, response.getText());
                        AnalysisSummary summary = result.getSummary();
                        ExpressionSummary expressionSummary = result.getExpression();
                        time = System.currentTimeMillis() - start;
                        handler.onResultSummaryLoaded(summary, expressionSummary, time);
                    } catch (AnalysisModelException e) {
                        e.printStackTrace();
                        handler.onResultSummaryError();
                    }
                    break;
                case Response.SC_NOT_FOUND:
                    time = System.currentTimeMillis() - start;
                    handler.onResultSummaryNotFound(time);
                    break;
                case Response.SC_GONE:
                    Console.error("Your result may have been deleted due to a new content release. " +
                            "Please submit your data again to obtain results for the latest version of our database");
                default:
                    Console.error(response.getStatusText());
                    handler.onResultSummaryError();
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            exception.printStackTrace();
            handler.onResultSummaryError();
        }
    }

    class PathwayIdentifiersLoader implements RequestCallback {
        long start = System.currentTimeMillis();
        AnalysisLoaderHandler handler;
        Request request;

        public PathwayIdentifiersLoader(AnalysisLoaderHandler handler, Long dbId, AnalysisStatus analysisStatus) {
            this.handler = handler;
            String url = PREFIX + analysisStatus.getToken() + "/summary/" + dbId + "?resource=" + analysisStatus.getResource();
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
            try {
                this.request = requestBuilder.sendRequest(null, this);
            } catch (RequestException e) {
                e.printStackTrace();
                eventBus.fireEventFromSource(new AnalysisResultErrorEvent(), AnalysisDataLoader.this);
            }
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            long time;
            switch (response.getStatusCode()) {
                case Response.SC_OK:
                    try {
                        PathwayIdentifiers pathwayIdentifiers = AnalysisModelFactory.getModelObject(PathwayIdentifiers.class, response.getText());
                        time = System.currentTimeMillis() - start;
                        handler.onPathwayIdentifiersLoaded(pathwayIdentifiers, time);
                    } catch (AnalysisModelException e) {
                        e.printStackTrace();
                        handler.onPathwayIdentifiersError();
                    }
                    break;
                case Response.SC_NOT_FOUND:
                    time = System.currentTimeMillis() - start;
                    handler.onPathwayIdentifiersNotFound(time);
                    break;
                case Response.SC_GONE:
                    Console.error("Your result may have been deleted due to a new content release. " +
                            "Please submit your data again to obtain results for the latest version of our database");
                default:
                    Console.error(response.getStatusText());
                    handler.onPathwayIdentifiersError();
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            exception.printStackTrace();
            handler.onPathwayIdentifiersError();
        }
    }

    class PathwaySummariesLoader implements RequestCallback {
        long start = System.currentTimeMillis();
        AnalysisLoaderHandler handler;
        Request request;

        public PathwaySummariesLoader(AnalysisLoaderHandler handler, Set<GraphPathway> graphPathways, AnalysisStatus analysisStatus) {
            this.handler = handler;
            String url = PREFIX + analysisStatus.getToken() + "/filter/pathways?resource=" + analysisStatus.getResource();
            StringBuilder postData = new StringBuilder();
            for (GraphPathway pathway : graphPathways) {
                postData.append(pathway.getDbId().toString()).append(",");
            }
            if (postData.length() > 0) postData.deleteCharAt(postData.length() - 1);

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            try {
                this.request = requestBuilder.sendRequest(postData.toString(), this);
            } catch (RequestException e) {
                e.printStackTrace();
                handler.onPathwaySummariesError();
            }
        }

        @Override
        public void onResponseReceived(Request request, Response response) {
            long time;
            switch (response.getStatusCode()) {
                case Response.SC_OK:
                    try {
                        List<PathwaySummary> pathwaySummaries = AnalysisModelFactory.getPathwaySummaryList(response.getText());
                        time = System.currentTimeMillis() - start;
                        handler.onPathwaySummariesLoaded(pathwaySummaries, time);
                    } catch (AnalysisModelException e) {
                        e.printStackTrace();
                        eventBus.fireEventFromSource(new AnalysisResultErrorEvent(), AnalysisDataLoader.this);
                    }
                    break;
                case Response.SC_NOT_FOUND:
                    time = System.currentTimeMillis() - start;
                    handler.onPathwaySummariesNotFound(time);
                    break;
                case Response.SC_GONE:
                    Console.error("Your result may have been deleted due to a new content release. " +
                            "Please submit your data again to obtain results for the latest version of our database");
                default:
                    Console.error(response.getStatusText());
                    handler.onPathwaySummariesError();
            }
        }

        @Override
        public void onError(Request request, Throwable exception) {
            exception.printStackTrace();
            handler.onPathwaySummariesError();
        }
    }
}
