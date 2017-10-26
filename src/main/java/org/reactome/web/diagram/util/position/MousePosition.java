package org.reactome.web.diagram.util.position;

import com.google.gwt.event.dom.client.MouseEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class MousePosition {

    private static IMousePosition mousePosition;

    public static void initialise(boolean isChrome) {
        if (mousePosition != null) {
            throw new RuntimeException("MousePosition has already been initialised. " +
                    "Only one initialisation is permitted per Diagram Viewer instance.");
        }
        if (isChrome) {
            mousePosition = new ChromeMousePosition();
        } else {
            mousePosition = new GenericMousePosition();
        }
    }

    public static IMousePosition get() {
        if (mousePosition == null) {
            throw new RuntimeException("MousePosition has not been initialised yet. " +
                    "Please call initialise before using 'get'");
        }
        return mousePosition;
    }

    public static int getX(MouseEvent event) {
        return mousePosition.getRelativeX(event);
    }

    public static int getY(MouseEvent event) {
        return mousePosition.getRelativeY(event);
    }
}
