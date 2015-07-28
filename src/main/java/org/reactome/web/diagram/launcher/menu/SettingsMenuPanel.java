package org.reactome.web.diagram.launcher.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;
import org.reactome.web.diagram.launcher.menu.submenu.AnalysisProfileMenuBar;
import org.reactome.web.diagram.launcher.menu.submenu.DiagramProfileMenuBar;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SettingsMenuPanel extends AbsolutePanel implements DiagramProfileMenuBar.DiagramProfileColourChangedHandler,
        AnalysisProfileMenuBar.AnalysisProfileColourChangedHandler,
        AboutMenuItem.AboutMenuItemSelectedHandler {

    private EventBus eventBus;

    public static final String MENU_ID = DOM.createUniqueId();
    private final PopupPanel popupPanel;

    public SettingsMenuPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().menuPanel());
        this.eventBus = eventBus;

        this.popupPanel = new PopupPanel(true, false);
        this.popupPanel.getElement().setId(MENU_ID);
        this.popupPanel.addStyleName(RESOURCES.getCSS().dropDownMenu());
        initMenu();
    }

    private void initMenu(){
        MenuBar menu = new MenuBar(true);
        menu.setAutoOpen(true); menu.setAnimationEnabled(true);

        menu.addItem("Colour profiles", new DiagramProfileMenuBar(this));
        menu.addItem("Analysis profiles", new AnalysisProfileMenuBar(this));
        menu.addItem(new AboutMenuItem(RESOURCES.aboutFireworks(), this));
        this.popupPanel.add(menu);
    }


    public void showRelativeTo(Button btn){
        System.out.println("going");
        this.popupPanel.showRelativeTo(btn);
        System.out.println("gone");
    }

    @Override
    public void onAboutMenuItemSelected() {
        this.popupPanel.hide();
    }

    @Override
    public void onDiagramProfileColourChanged(DiagramProfile profile) {
        this.popupPanel.hide();
        this.eventBus.fireEventFromSource(new DiagramProfileChangedEvent(profile), this);
    }

    @Override
    public void onAnalysisProfileColourChanged(AnalysisProfile profile) {
        this.popupPanel.hide();
        this.eventBus.fireEventFromSource(new AnalysisProfileChangedEvent(profile), this);
    }


    public static SettingsMenuResources RESOURCES;
    static {
        RESOURCES = GWT.create(SettingsMenuResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface SettingsMenuResources extends ClientBundle {

        @Source(SettingsMenuPanelCSS.CSS)
        SettingsMenuPanelCSS getCSS();

        @Source("aboutDiagram.html")
        TextResource aboutFireworks();

    }

    @CssResource.ImportedWithPrefix("diagram-SettingsMenuPanel")
    public interface SettingsMenuPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/launcher/menu/MenuPanel.css";

        String menuPanel();

        String dropDownMenu();
    }
}
