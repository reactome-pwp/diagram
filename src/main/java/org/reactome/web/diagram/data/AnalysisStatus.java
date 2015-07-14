package org.reactome.web.diagram.data;

import com.google.gwt.http.client.URL;
import org.reactome.web.diagram.data.analysis.AnalysisSummary;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.analysis.ExpressionSummary;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisStatus {

    private String token;
    private String resource;

    private AnalysisSummary analysisSummary;
    private ExpressionSummary expressionSummary;

    public AnalysisStatus(String token, String resource) {
        if(token==null) throw new RuntimeException("token cannot be null");
        this.token = URL.decode(token);
        this.resource = resource==null?"TOTAL":resource;
    }

    public String getToken() {
        return token;
    }

    public String getResource() {
        return resource;
    }

    public AnalysisType getAnalysisType(){
        return AnalysisType.getType(analysisSummary.getType());
    }

    public AnalysisSummary getAnalysisSummary() {
        return analysisSummary;
    }

    public void setAnalysisSummary(AnalysisSummary analysisSummary) {
        this.analysisSummary = analysisSummary;
    }

    public ExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public void setExpressionSummary(ExpressionSummary expressionSummary) {
        this.expressionSummary = expressionSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisStatus that = (AnalysisStatus) o;

        return !(token != null ? !token.equals(that.token) : that.token != null) &&
                !(resource != null ? !resource.equals(that.resource) : that.resource != null);

    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnalysisStatus{" +
                "token='" + token + '\'' +
                ", resource='" + resource + '\'' +
                '}';
    }
}
