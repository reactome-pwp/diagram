package org.reactome.web.diagram.launcher.menu.submenu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.launcher.menu.SettingsMenuPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class SubMenuBar extends MenuBar {

    public SubMenuBar(boolean vertical) {
        super(vertical);
        relocateSubMenu();
    }

    /**
     * This is done because there is not way of accessing the popup-panel containing
     * the profile sub-menu.
     * By default, the subMenu **ALWAYS** appears on the RIGHT HAND SIDE of the main
     * menu, but we want it to appear on the LEFT HAND SIDE.
     * To do so, the easiest way I have found is climbing up
     * the DOM until the last guy under body from the guy who gets attached in the
     * DOM when he user selects that option in the menu.
     * Getting the position of the main menu popup is easier ;)
     */
    protected void relocateSubMenu(){
        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(final AttachEvent event) {
                if(event.isAttached()){
                    Widget w = (Widget) event.getSource();
                    do{
                        w = w.getParent();
                    }while(!w.getParent().toString().contains("<body"));
                    final Widget target = w;

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            Element aux = DOM.getElementById(SettingsMenuPanel.MENU_ID);
                            int left = aux.getAbsoluteLeft();
                            int right = Window.getClientWidth() - left;

                            Style style = target.getElement().getStyle();
                            style.clearLeft();
                            style.setRight(right - 4, Style.Unit.PX);
                        }
                    });
                }
            }
        });
    }
}
