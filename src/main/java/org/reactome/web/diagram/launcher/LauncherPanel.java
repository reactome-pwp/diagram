package org.reactome.web.diagram.launcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;
import org.reactome.web.diagram.launcher.controls.MainControlPanel;
import org.reactome.web.diagram.search.SearchPanel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LauncherPanel extends FlowPanel implements DiagramLoadedHandler, DiagramRequestedHandler, LayoutLoadedHandler {

    public LauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());

        //Search panel
        this.add(new SearchPanel(eventBus));
        //Main Control panel
        this.add(new MainControlPanel(eventBus));

        this.setVisible(false);

        eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setVisible(true);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }
    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SearchLauncher")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/launcher/LauncherPanel.css";

        String launcherPanel();
    }
}
