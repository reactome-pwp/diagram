package org.reactome.web.diagram.launcher.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.launcher.LauncherButton;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MainControlPanel extends FlowPanel implements ClickHandler {

    private EventBus eventBus;
    private LauncherButton fitAll;
    private LauncherButton fireworks;

    public MainControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        this.addStyleName(RESOURCES.getCSS().mainControlPanel());
        this.fitAll = new LauncherButton("Show all", RESOURCES.getCSS().fitall(), this);
        this.fireworks = new LauncherButton("Pathway overview", RESOURCES.getCSS().fireworks(), this);

        this.add(this.fitAll);
        this.add(this.fireworks);
    }

    @Override
    public void onClick(ClickEvent event) {
        LauncherButton btn = (LauncherButton) event.getSource();
        if(btn.equals(this.fitAll)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.FIT_ALL), this);
        }else if(btn.equals(this.fireworks)){
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.FIREWORKS), this);
        }
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

        @Source("images/fitall_clicked.png")
        ImageResource fitallClicked();

        @Source("images/fitall_disabled.png")
        ImageResource fitallDisabled();

        @Source("images/fitall_hovered.png")
        ImageResource fitallHovered();

        @Source("images/fitall_normal.png")
        ImageResource fitallNormal();

        @Source("images/fireworks_clicked.png")
        ImageResource fireworksClicked();

        @Source("images/fireworks_disabled.png")
        ImageResource fireworksDisabled();

        @Source("images/fireworks_hovered.png")
        ImageResource fireworksHovered();

        @Source("images/fireworks_normal.png")
        ImageResource fireworksNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/launcher/controls/MainControlPanel.css";

        String mainControlPanel();

        String fitall();

        String fireworks();
    }
}
