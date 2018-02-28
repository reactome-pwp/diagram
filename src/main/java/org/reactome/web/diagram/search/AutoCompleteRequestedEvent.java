package org.reactome.web.diagram.search;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AutoCompleteRequestedEvent extends GwtEvent<AutoCompleteRequestedHandler> {
    public static Type<AutoCompleteRequestedHandler> TYPE = new Type<>();

    private String term;

    public AutoCompleteRequestedEvent(String term) {
        this.term = term;
    }

    @Override
    public Type<AutoCompleteRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AutoCompleteRequestedHandler handler) {
        handler.onAutoCompleteRequested(this);
    }

    public String getTerm() {
        return term;
    }
}
