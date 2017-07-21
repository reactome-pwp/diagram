package org.reactome.web.diagram.controls.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.common.ExpandibleContainer;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.controls.top.common.AbstractMenuDialog;
import org.reactome.web.diagram.controls.top.illustrations.DiagramIllustrations;
import org.reactome.web.diagram.controls.top.key.DiagramKey;
import org.reactome.web.diagram.controls.top.key.EHLDKey;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;
import static org.reactome.web.diagram.data.content.Content.Type.SVG;
import static org.reactome.web.diagram.events.CanvasExportRequestedEvent.Option.IMAGE;
import static org.reactome.web.diagram.events.CanvasExportRequestedEvent.Option.PPTX;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class RightTopLauncherPanel extends FlowPanel implements ClickHandler, ContentLoadedHandler {

    private EventBus eventBus;

    private DiagramIllustrations diagramIllustrations;
    private AbstractMenuDialog diagramKey;
    private Map<Content.Type, AbstractMenuDialog> keys;

    private PwpButton illustrationsBtn;
    private PwpButton captureBtn;
    private PwpButton exportBtn;
    private PwpButton diagramKeyBtn;

    private ExpandibleContainer expContainer;

    public RightTopLauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());
        this.eventBus = eventBus;

        this.keys = new HashMap<>();
        this.keys.put(DIAGRAM, new DiagramKey(eventBus));
        this.keys.put(SVG, new EHLDKey(eventBus));
        this.diagramKey = keys.get(SVG);

        this.diagramIllustrations = new DiagramIllustrations(eventBus);

        this.illustrationsBtn = new PwpButton("Show illustrations", RESOURCES.getCSS().illustrations(), this);
        this.add(illustrationsBtn);

        this.captureBtn = new PwpButton("Export image", RESOURCES.getCSS().camera(), this);

        this.exportBtn = new PwpButton("Export to pptx", RESOURCES.getCSS().exportPPT(), this);

        expContainer = new ExpandibleContainer("Select one export option", RESOURCES.getCSS().export());
        expContainer.addButton(captureBtn);
        expContainer.addButton(exportBtn);
        add(expContainer);

        this.diagramKeyBtn = new PwpButton("Diagram key", RESOURCES.getCSS().key(), this);
        this.add(this.diagramKeyBtn);

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.setVisible(true);
    }

    @Override
    public void onClick(ClickEvent event) {
        PwpButton btn = (PwpButton) event.getSource();
        if (btn.equals(this.captureBtn)) {
            expContainer.collapse();
            this.eventBus.fireEventFromSource(new CanvasExportRequestedEvent(IMAGE), this);
        } else if (btn.equals(this.diagramKeyBtn)) {
            if (this.diagramKey.isShowing()) {
                this.diagramKey.hide();
            } else {
                this.diagramKey.showRelativeTo(this.diagramKeyBtn);
            }
        } else if (btn.equals(this.illustrationsBtn)) {
            if (this.diagramIllustrations.isShowing()) {
                this.diagramIllustrations.hide();
            } else {
                this.diagramIllustrations.showRelativeTo(btn);
            }
        } else if (btn.equals(this.exportBtn)) {
            expContainer.collapse();
            this.eventBus.fireEventFromSource(new CanvasExportRequestedEvent(PPTX), this);
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        switch (event.getContext().getContent().getType()) {
            case DIAGRAM:
                exportBtn.setVisible(true);
                illustrationsBtn.setEnabled(true);
                break;
            case SVG:
                exportBtn.setVisible(false);
                illustrationsBtn.setEnabled(false);
                if (this.diagramIllustrations.isShowing()) {
                    this.diagramIllustrations.hide();
                }
                break;
        }
        diagramKey.hide();
        diagramKey = keys.get(event.getContext().getContent().getType());

    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();


        @Source("images/camera_clicked.png")
        ImageResource cameraClicked();

        @Source("images/camera_disabled.png")
        ImageResource cameraDisabled();

        @Source("images/camera_hovered.png")
        ImageResource cameraHovered();

        @Source("images/camera_normal.png")
        ImageResource cameraNormal();

        @Source("images/illustrations_clicked.png")
        ImageResource illustrationsClicked();

        @Source("images/illustrations_disabled.png")
        ImageResource illustrationsDisabled();

        @Source("images/illustrations_hovered.png")
        ImageResource illustrationsHovered();

        @Source("images/illustrations_normal.png")
        ImageResource illustrationsNormal();

        @Source("images/key_clicked.png")
        ImageResource keyClicked();

        @Source("images/key_disabled.png")
        ImageResource keyDisabled();

        @Source("images/key_hovered.png")
        ImageResource keyHovered();

        @Source("images/key_normal.png")
        ImageResource keyNormal();

        @Source("images/export_hovered.png")
        ImageResource exportHovered();

        @Source("images/export_normal.png")
        ImageResource exportNormal();

        @Source("images/export2ppt_clicked.png")
        ImageResource export2pptClicked();

        @Source("images/export2ppt_disabled.png")
        ImageResource export2pptDisabled();

        @Source("images/export2ppt_hovered.png")
        ImageResource export2pptHovered();

        @Source("images/export2ppt_normal.png")
        ImageResource export2pptNormal();

    }

    @CssResource.ImportedWithPrefix("diagram-LeftTopLauncher")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/top/RightTopLauncherPanel.css";

        String launcherPanel();

        String camera();

        String exportPPT();

        String export();

        String illustrations();

        String key();
    }
}
