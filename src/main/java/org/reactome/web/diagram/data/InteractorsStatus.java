package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.layout.SummaryItem;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsStatus {
    private boolean loading = true;
    private String resource;
    private Set<String> burstEntities;
    private double threshold = 0.5;
    private String serverMsg;

    public InteractorsStatus() {
        this.burstEntities = new HashSet<>();
    }

    public void onBurstToggle(SummaryItem summaryItem, String identifier) {
        if (summaryItem!=null && summaryItem.getType().equals("TR")) {
            if (summaryItem.getPressed()) {
                burstEntities.add(identifier);
            } else {
                burstEntities.remove(identifier);
            }
        }
    }

    public void clearBurstEntities(){
        this.burstEntities.clear();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isVisible() {
        return !this.burstEntities.isEmpty();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getServerMsg() {
        return serverMsg;
    }

    public void setServerMsg(String serverMsg) {
        this.serverMsg = serverMsg;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "InteractorsStatus{" +
                "resource='" + resource + '\'' +
                ", serverMsg='" + serverMsg + '\'' +
                ", loading=" + loading +
                ", threshold=" + threshold +
                ", isVisible=" + isVisible() +
                '}';
    }
}
