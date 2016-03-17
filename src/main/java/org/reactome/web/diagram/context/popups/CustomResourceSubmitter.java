package org.reactome.web.diagram.context.popups;

import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;
import org.reactome.web.diagram.data.interactors.custom.raw.RawUploadResponse;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseException;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseFactory;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class CustomResourceSubmitter implements FormPanel.SubmitHandler, FormPanel.SubmitCompleteHandler, RequestCallback {

    public interface Handler {
        void onSubmissionCompleted(RawUploadResponse response, long time);
        void onSubmissionError(UploadResponseException exception);
    }

    private Handler handler;
    private Request request;

    public CustomResourceSubmitter(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(request!=null && request.isPending()){
            request.cancel();
        }
    }

    public void submit(String data, String url){
        // Any previous request has to be canceled
        cancel();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
        requestBuilder.setHeader("Content-Type", "text/plain");
        requestBuilder.setHeader("Accept", "application/json");
        try {
            this.request = requestBuilder.sendRequest(data, this);
        } catch (RequestException ex) {
            handler.onSubmissionError(new UploadResponseException());
        }
    }

    public void submit(FormPanel formPanel, String url){
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
        formPanel.setAction(url);
        formPanel.addSubmitHandler(this);
        formPanel.addSubmitCompleteHandler(this);
        formPanel.submit();
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                RawUploadResponse uploadResponse;
                try {
                    uploadResponse = UploadResponseFactory.getUploadResponseObject(RawUploadResponse.class, response.getText());
                } catch (UploadResponseException e) {
                    this.handler.onSubmissionError(e);
                    return;
                }
                long time = System.currentTimeMillis() - start;

                this.handler.onSubmissionCompleted(uploadResponse, time);
                break;
            default:
                this.handler.onSubmissionError(new UploadResponseException());
        }
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        this.handler.onSubmissionError(new UploadResponseException());
    }

    @Override
    public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
        //Work around to extract the content in case it's included in a HTML tag
        Element label = DOM.createLabel();
        label.setInnerHTML( event.getResults() );
        long start = System.currentTimeMillis();
        RawUploadResponse uploadResponse;
        try {
            uploadResponse = UploadResponseFactory.getUploadResponseObject(RawUploadResponse.class, label.getInnerText());
        } catch (UploadResponseException e) {
            this.handler.onSubmissionError(e);
            return;
        }
        long time = System.currentTimeMillis() - start;
        this.handler.onSubmissionCompleted(uploadResponse, time);
    }

    @Override
    public void onSubmit(FormPanel.SubmitEvent event) {

    }

}
