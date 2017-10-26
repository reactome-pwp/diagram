package org.reactome.web.diagram.util.position;

import com.google.gwt.event.dom.client.MouseEvent;

/**
 * Fixes a bug detected in Chrome v61 and 62.
 * Detects whether Chrome is used and applies the correct mouse
 * position calculation by instantiating and using the proper class.
 *
 * Important Note: This can be removed as soon as the bug is fixed
 * in future releases of Chrome
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class MousePosition {

    private static IMousePosition mousePosition;

    static {
        if (isChrome()) {
            mousePosition = new ChromeMousePosition();
        } else {
            mousePosition = new GenericMousePosition();
        }
    }

    public static int getX(MouseEvent event) {
        return mousePosition.getRelativeX(event);
    }

    public static int getY(MouseEvent event) {
        return mousePosition.getRelativeY(event);
    }

    private static native boolean isChrome()/*-{
        var isChromium = window.chrome,
            winNav = window.navigator,
            vendorName = winNav.vendor,
            isOpera = winNav.userAgent.indexOf("OPR") > -1,
            isIEedge = winNav.userAgent.indexOf("Edge") > -1,
            isIOSChrome = winNav.userAgent.match("CriOS");

        if (isIOSChrome) {
            return true;
        } else if (
            isChromium !== null &&
            typeof isChromium !== "undefined" &&
            vendorName === "Google Inc." &&
            isOpera === false &&
            isIEedge === false
        ) {
            return true;
        } else {
            return false;
        }
    }-*/;
}
