package org.reactome.web.diagram.data;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsStatus {
    private boolean loading = true;
    private String resource;
    private double threshold = 0.5;
    private String serverMsg;
    private boolean visible;

    public InteractorsStatus(String resource) {
        setResource(resource);
    }


    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "InteractorsStatus{" +
                "resource='" + resource + '\'' +
                ", serverMsg='" + serverMsg + '\'' +
                ", loading=" + loading +
                ", threshold=" + threshold +
                ", isVisible=" + visible +
                '}';
    }
}
