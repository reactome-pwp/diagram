package org.reactome.web.diagram.util.position;

import com.google.gwt.event.dom.client.MouseEvent;

/**
 * This class is used to provide the correct mouse position within the widget
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface IMousePosition {

    default int getRelativeX(MouseEvent event) {
        return event.getRelativeX(event.getRelativeElement());
    }

    default int getRelativeY(MouseEvent event) {
        return event.getRelativeY(event.getRelativeElement());
    }
}
