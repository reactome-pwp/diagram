package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.SuggestionResetHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public class SuggestionResetEvent extends GwtEvent<SuggestionResetHandler> {
    public static Type<SuggestionResetHandler> TYPE = new Type<>();

    @Override
    public Type<SuggestionResetHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SuggestionResetHandler handler) {
        handler.onSuggestionReset(this);
    }

    @Override
    public String toString() {
        return "SuggestionResetEvent{}";
    }
}
