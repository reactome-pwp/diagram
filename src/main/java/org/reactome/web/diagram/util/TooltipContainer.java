package org.reactome.web.diagram.util;

import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class TooltipContainer extends AbsolutePanel {

    private int width;
    private int height;

    public TooltipContainer(int width, int height) {
        super();
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(int width) {
        setWidth(width + "px");
        this.width = width;
    }

    public void setHeight(int height) {
        setHeight(height + "px");
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
