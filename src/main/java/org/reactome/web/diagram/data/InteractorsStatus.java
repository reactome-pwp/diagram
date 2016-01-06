package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.MapSet;

import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsStatus {
    private boolean loading = true;
    private String resource;
    private MapSet<String, String> burstEntities;
    private double threshold = 0.5;
    private String serverMsg;

    public InteractorsStatus(String resource) {
        setResource(resource);
        this.burstEntities = new MapSet<>();
    }

    public void onBurstToggle(SummaryItem summaryItem, String resource, String identifier) {
        resource = resource.toLowerCase();
        if (summaryItem!=null && summaryItem.getType().equals("TR")) {
            if (summaryItem.getPressed()) {
                burstEntities.add(resource, identifier);
            } else {
                burstEntities.remove(resource, identifier);
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
        String resource = this.resource.toLowerCase();
        Set<String> elems = this.burstEntities.getElements(resource);
        return elems!=null && !elems.isEmpty();
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
