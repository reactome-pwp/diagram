package org.reactome.web.diagram.data.loader;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.ParserException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGLoader implements RequestCallback {

    public interface Handler {
        void onSvgLoaded(String stId, OMSVGSVGElement svg, long time);

        void onSvgLoaderError(String stId, Throwable exception);
    }

    private static String PREFIX = DiagramFactory.SERVER + "/download/current/ehld/";
    private static String SUFFIX = "?v=" + LoaderManager.version;
    private static boolean BROWSER_SUPPORTED = true;

    static {
        // EHLDs are not supported for IE11 and for Safari only in case the base tag of the document has been altered.
        BROWSER_SUPPORTED = !isIE11() && !(isSafari() && containsBaseTag());
    }

    private Handler handler;
    private Request request;
    private String stId;

    SVGLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel() {
        if (this.request != null && this.request.isPending()) {
            this.stId = null;
            this.request.cancel();
        }
    }

    void load(String stId) {
        this.stId = stId;
        if (!stId.endsWith(".svg")) stId = stId + ".svg";

        String url = PREFIX + stId + SUFFIX;
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

    public static boolean isSVGAvailable(String identifier) {
        //If availableSVG is null, we cannot ensure the SVG isn't available because the data is not yet retrieved
        return BROWSER_SUPPORTED && (availableSVG == null || availableSVG.contains(identifier));
    }

    private static Set<String> availableSVG = null;

    static {
        String url = PREFIX + "svgsummary.txt" + SUFFIX;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    availableSVG = new HashSet<>();
                    switch (response.getStatusCode()) {
                        case Response.SC_OK:
                            Collections.addAll(availableSVG, response.getText().split("\\n"));
                            break;
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    availableSVG = new HashSet<>();
                }
            });
        } catch (RequestException e) {
            availableSVG = new HashSet<>();
        }
    }

    private static boolean containsBaseTag() {
        NodeList<Element> list = Document.get().getElementsByTagName("base");
        return (list != null && list.getLength() > 0);
    }

    private static native boolean isIE11()/*-{
        // true on IE11 but false on Edge and other IEs/browsers (source https://stackoverflow.com/questions/21825157/internet-explorer-11-detection).
        return !!window.MSInputMethodContext && !!document.documentMode;

        //return !!navigator.userAgent.match(/Trident\/7\./);  //B plan
    }-*/;

    private static native boolean isSafari()/*-{
        // true on Safari browsers (source https://stackoverflow.com/questions/7944460/detect-safari-browser).
        var ua = navigator.userAgent.toLowerCase();
        var isSafari = false;
        try {
            isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || safari.pushNotification);
        } catch(err) {}
        isSafari = (isSafari || ((ua.indexOf('safari') != -1)&& (!(ua.indexOf('chrome')!= -1) && (ua.indexOf('version/')!= -1))));
        return isSafari;
    }-*/;
}
