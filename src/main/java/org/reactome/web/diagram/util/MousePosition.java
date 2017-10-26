package org.reactome.web.diagram.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseEvent;

/**
 * This class is used to provide the correct mouse position within the widget
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class MousePosition {

    public static int getRelativeX(MouseEvent event) {
        NativeEvent e = event.getNativeEvent();
        Element target = event.getRelativeElement();
        return e.getClientX() - target.getAbsoluteLeft();
    }

    public static int getRelativeY(MouseEvent event) {
        NativeEvent e = event.getNativeEvent();
        Element target = event.getRelativeElement();
        return e.getClientY() - target.getAbsoluteTop();
    }
}
