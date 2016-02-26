package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.analysis.client.model.*;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisResultLoadedEvent extends GwtEvent<AnalysisResultLoadedHandler> {
    public static Type<AnalysisResultLoadedHandler> TYPE = new Type<AnalysisResultLoadedHandler>();

    private AnalysisSummary summary;
    private ExpressionSummary expressionSummary;
    private PathwayIdentifiers pathwayIdentifiers;
    private List<PathwaySummary> pathwaySummaries;
    private long time;

    public AnalysisResultLoadedEvent(AnalysisSummary summary, ExpressionSummary expressionSummary, PathwayIdentifiers pathwayIdentifiers, List<PathwaySummary> pathwaySummaries, long time) {
        this.summary = summary;
        this.expressionSummary = expressionSummary;
        this.pathwayIdentifiers = pathwayIdentifiers;
        this.pathwaySummaries = pathwaySummaries == null ? new LinkedList<PathwaySummary>() : pathwaySummaries;
        this.time = time;
    }

    @Override
    public Type<AnalysisResultLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisResultLoadedHandler handler) {
        handler.onAnalysisResultLoaded(this);
    }

    public PathwayIdentifiers getPathwayIdentifiers() {
        return pathwayIdentifiers;
    }

    public ExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public List<PathwaySummary> getPathwaySummaries() {
        return pathwaySummaries;
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public AnalysisType getType() {
        return AnalysisType.getType(this.summary.getType());
    }

    public boolean isReset() {
        return summary == null;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "AnalysisResultLoadedEvent{" +
                "time=" + time +
                ", pathwayIdentifiers=" + (pathwayIdentifiers != null ? pathwayIdentifiers.getIdentifiers().size() : "null") +
                ", type=" + (summary != null ?  summary.getType() : "null") +
                ", resource=" + (pathwayIdentifiers != null ? pathwayIdentifiers.getResources() : "null") +
                '}';
    }
}
