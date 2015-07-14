package org.reactome.web.diagram.controls;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ControlPanel extends AbsolutePanel implements ClickHandler,
        DiagramRequestedHandler, LayoutLoadedHandler, DiagramLoadedHandler {

    protected EventBus eventBus;

    private ControlButton zoomIn;
    private ControlButton zoomOut;
    private ControlButton fitAll;
    private ControlButton up;
    private ControlButton right;
    private ControlButton down;
    private ControlButton left;
    private ControlButton fireworks;

    public ControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.initHandlers();

        //Setting the legend style
        getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        setStyleName(RESOURCES.getCSS().controlPanel());

        ControlPanelCSS css = RESOURCES.getCSS();

        this.zoomIn = new ControlButton("Zoom in", css.zoomIn(), this);
        this.add(this.zoomIn);
        this.zoomOut = new ControlButton("Zoom out", css.zoomOut(), this);
        this.add(this.zoomOut);

        this.fitAll = new ControlButton("Show all", css.fitall(), this);
        this.add(this.fitAll);

        this.up = new ControlButton("Move up", css.up(), this);
        this.add(this.up);
        this.right = new ControlButton("Move right", css.right(), this);
        this.add(this.right);
        this.down = new ControlButton("Move down", css.down(), this);
        this.add(this.down);
        this.left = new ControlButton("Move left", css.left(), this);
        this.add(this.left);

        this.fireworks = new ControlButton("Show pathway overview", css.open(), this);
        this.add(this.fireworks);

        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        ControlAction action = ControlAction.NONE;
        ControlButton btn = (ControlButton) event.getSource();
        if (btn.equals(this.zoomIn)) {
            action = ControlAction.ZOOM_IN;
        } else if (btn.equals(this.zoomOut)) {
            action = ControlAction.ZOOM_OUT;
        } else if (btn.equals(this.fitAll)) {
            action = ControlAction.FIT_ALL;
        } else if (btn.equals(this.up)) {
            action = ControlAction.UP;
        } else if (btn.equals(this.right)) {
            action = ControlAction.RIGHT;
        } else if (btn.equals(this.down)) {
            action = ControlAction.DOWN;
        } else if (btn.equals(this.left)) {
            action = ControlAction.LEFT;
        } else if (btn.equals(this.fireworks)) {
            action = ControlAction.FIREWORKS;
        }
        if (!action.equals(ControlAction.NONE)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(action), this);
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setVisible(true);
    }

    private void initHandlers() {
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }


    public static ControlResources RESOURCES;

    static {
        RESOURCES = GWT.create(ControlResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface ControlResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ControlPanelCSS.CSS)
        ControlPanelCSS getCSS();

        @Source("images/down_clicked.png")
        ImageResource downClicked();

        @Source("images/down_disabled.png")
        ImageResource downDisabled();

        @Source("images/down_hovered.png")
        ImageResource downHovered();

        @Source("images/down_normal.png")
        ImageResource downNormal();

        @Source("images/fitall_clicked.png")
        ImageResource fitallClicked();

        @Source("images/fitall_disabled.png")
        ImageResource fitallDisabled();

        @Source("images/fitall_hovered.png")
        ImageResource fitallHovered();

        @Source("images/fitall_normal.png")
        ImageResource fitallNormal();

        @Source("images/left_clicked.png")
        ImageResource leftClicked();

        @Source("images/left_disabled.png")
        ImageResource leftDisabled();

        @Source("images/left_hovered.png")
        ImageResource leftHovered();

        @Source("images/left_normal.png")
        ImageResource leftNormal();

        @Source("images/open_clicked.png")
        ImageResource openClicked();

        @Source("images/open_disabled.png")
        ImageResource openDisabled();

        @Source("images/open_hovered.png")
        ImageResource openHovered();

        @Source("images/open_normal.png")
        ImageResource openNormal();

        @Source("images/right_clicked.png")
        ImageResource rightClicked();

        @Source("images/right_disabled.png")
        ImageResource rightDisabled();

        @Source("images/right_hovered.png")
        ImageResource rightHovered();

        @Source("images/right_normal.png")
        ImageResource rightNormal();

        @Source("images/up_clicked.png")
        ImageResource upClicked();

        @Source("images/up_disabled.png")
        ImageResource upDisabled();

        @Source("images/up_hovered.png")
        ImageResource upHovered();

        @Source("images/up_normal.png")
        ImageResource upNormal();

        @Source("images/zoomin_clicked.png")
        ImageResource zoomInClicked();

        @Source("images/zoomin_disabled.png")
        ImageResource zoomInDisabled();

        @Source("images/zoomin_hovered.png")
        ImageResource zoomInHovered();

        @Source("images/zoomin_normal.png")
        ImageResource zoomInNormal();

        @Source("images/zoomout_clicked.png")
        ImageResource zoomOutClicked();

        @Source("images/zoomout_disabled.png")
        ImageResource zoomOutDisabled();

        @Source("images/zoomout_hovered.png")
        ImageResource zoomOutHovered();

        @Source("images/zoomout_normal.png")
        ImageResource zoomOutNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ControlPanel")
    public interface ControlPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/ControlPanel.css";

        String controlPanel();

        String down();

        String fitall();

        String left();

        String right();

        String up();

        String open();

        String zoomIn();

        String zoomOut();
    }
}
