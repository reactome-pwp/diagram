package org.reactome.web.diagram.controls.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.controls.navigation.MainControlPanel;
import org.reactome.web.diagram.controls.top.search.SearchPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LeftTopLauncherPanel extends FlowPanel {

    public LeftTopLauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());

        //Search panel
        this.add(new SearchPanel(eventBus));
        //Main Control panel
        this.add(new MainControlPanel(eventBus));

        this.setVisible(true);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-LeftTopLauncher")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/top/LeftTopLauncherPanel.css";

        String launcherPanel();
    }
}
