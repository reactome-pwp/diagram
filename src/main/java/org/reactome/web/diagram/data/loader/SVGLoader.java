package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.ParserException;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGLoader implements RequestCallback {

    public interface Handler {
        void onSvgLoaded(String stId, OMSVGSVGElement svg, long time);
        void onSvgLoaderError(String stId, Throwable exception);
    }

    private static String PREFIX = DiagramFactory.SERVER + "/svg/";
    private static String SUFFIX = "?v=" + LoaderManager.version;


    private Handler handler;
    private Request request;
    private String stId;

    SVGLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.stId = null;
            this.request.cancel();
        }
    }

    void load(String stId){
        this.stId = stId;
        
        if(!stId.endsWith(".svg")) {
            stId = stId + ".svg";
        }

        String url = PREFIX + stId +  SUFFIX;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onSvgLoaderError(stId, e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                try {
                    long start = System.currentTimeMillis();
                    OMSVGSVGElement svg = OMSVGParser.parse(response.getText(), false);
                    long time = System.currentTimeMillis() - start;
                    this.handler.onSvgLoaded(stId, svg, time);
                } catch (ParserException e) {
                    this.handler.onSvgLoaderError(stId, e);
                }
                break;
            default:
                this.handler.onSvgLoaderError(stId, new Exception(response.getStatusText()));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onSvgLoaderError(stId, exception);
    }
}
