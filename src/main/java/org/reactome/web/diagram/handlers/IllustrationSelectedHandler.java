package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.IllustrationSelectedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface IllustrationSelectedHandler extends EventHandler {
    void onIllustrationSelected(IllustrationSelectedEvent event);
}
