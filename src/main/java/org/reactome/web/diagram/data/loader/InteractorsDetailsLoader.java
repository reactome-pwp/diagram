package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDetailsLoader extends InteractorsLoader {

    public InteractorsDetailsLoader(Handler handler) {
        super(handler);
    }

    public void load(String stId, String resource){
        if(resource==null){
            this.handler.onInteractorsLoaderError(new InteractorsException("Resource not specified"));
            return;
        }
        String url = PREFIX + resource + "/" + stId + ".details.json?v=" + LoaderManager.version;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onInteractorsLoaderError(e);
        }
    }

}
