package org.reactome.web.diagram.util.position;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * Wraps around the {@link MouseEvent#getRelativeX(Element) getRelativeX()} and
 * {@link MouseEvent#getRelativeY(Element) getRelativeY()} and provides them as
 * default implementations.
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
