package org.reactome.web.diagram.controls.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;
import org.reactome.web.diagram.handlers.CanvasExportRequestedHandler;

import static org.reactome.web.diagram.events.CanvasExportRequestedEvent.Option.PPTX;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class NotificationsContainer extends FlowPanel implements CanvasExportRequestedHandler {

    public NotificationsContainer(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().container());

        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);
        this.setVisible(true);
    }

    @Override
    public void onDiagramExportRequested(CanvasExportRequestedEvent event) {
        if(event.getOption().equals(PPTX)) {
            Notification notification = new ExpiringNotification("Your download will be completed shortly", 2000);
            add(notification);
            notification.display();
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
    }

    @CssResource.ImportedWithPrefix("diagram-NotificationsContainer")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/notifications/NotificationsContainer.css";

        String container();

    }
}
