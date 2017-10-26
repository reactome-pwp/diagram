package org.reactome.web.diagram.util.position;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * A concreat implementation of {@link IMousePosition} providing an alternative calculation of the
 * relative mouse position. This is meant to fix a bug in Chrome v62.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ChromeMousePosition implements IMousePosition {
    @Override
    public int getRelativeX(MouseEvent event) {
        NativeEvent e = event.getNativeEvent();
        Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft();
    }

    @Override
    public int getRelativeY(MouseEvent event) {
        NativeEvent e = event.getNativeEvent();
        Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop();
    }
}
