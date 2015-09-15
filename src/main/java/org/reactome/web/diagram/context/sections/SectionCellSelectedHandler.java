package org.reactome.web.diagram.context.sections;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SectionCellSelectedHandler extends EventHandler {
    void onCellSelected(SectionCellSelectedEvent event);
}