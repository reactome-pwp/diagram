package org.reactome.web.diagram.menu;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SettingsButton extends Button {

    public SettingsButton(String title, String style, ClickHandler handler) {
        setStyleName(style);
        addClickHandler(handler);
        setTitle(title);
    }
}
