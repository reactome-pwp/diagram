package org.reactome.web.diagram.launcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.launcher.key.DiagramKey;
import org.reactome.web.diagram.launcher.menu.SettingsMenuPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class RightTopLauncherPanel extends FlowPanel implements ClickHandler {

    private DiagramKey diagramKey;
    private SettingsMenuPanel settings;

    private PwpButton diagramKeyBtn;
    private PwpButton settingBtn;

    public RightTopLauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());

        this.diagramKey = new DiagramKey(eventBus);
        this.settings = new SettingsMenuPanel(eventBus);

        this.diagramKeyBtn = new PwpButton("Diagram key", RESOURCES.getCSS().key(), this);
        this.add(this.diagramKeyBtn);

        this.settingBtn = new PwpButton("Settings", RESOURCES.getCSS().settings(), this);
        this.add(this.settingBtn);

        this.setVisible(true);
    }

    @Override
    public void onClick(ClickEvent event) {
        PwpButton btn = (PwpButton) event.getSource();
        if (btn.equals(this.diagramKeyBtn)) {
            if (this.diagramKey.isShowing()) {
                this.diagramKey.hide();
            } else {
                this.diagramKey.showRelativeTo(this.diagramKeyBtn);
            }
        } else if (btn.equals(this.settingBtn)) {
            this.settings.showRelativeTo(btn);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("key/images/key_clicked.png")
        ImageResource keyClicked();

        @Source("key/images/key_disabled.png")
        ImageResource keyDisabled();

        @Source("key/images/key_hovered.png")
        ImageResource keyHovered();

        @Source("key/images/key_normal.png")
        ImageResource keyNormal();

        @Source("menu/images/settings_clicked.png")
        ImageResource settingsClicked();

        @Source("menu/images/settings_disabled.png")
        ImageResource settingsDisabled();

        @Source("menu/images/settings_hovered.png")
        ImageResource settingsHovered();

        @Source("menu/images/settings_normal.png")
        ImageResource settingsNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-LeftTopLauncher")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/launcher/RightTopLauncherPanel.css";

        String launcherPanel();

        String key();

        String settings();
    }
}
