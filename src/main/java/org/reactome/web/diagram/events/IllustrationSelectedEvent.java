package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.IllustrationSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class IllustrationSelectedEvent extends GwtEvent<IllustrationSelectedHandler> {
    public static final Type<IllustrationSelectedHandler> TYPE = new Type<>();

    private String url;

    public IllustrationSelectedEvent(String url) {
        this.url = url;
    }

    @Override
    public Type<IllustrationSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(IllustrationSelectedHandler handler) {
        handler.onIllustrationSelected(this);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "IllustrationSelectedEvent{" +
                "url='" + url + '\'' +
                '}';
    }
}
