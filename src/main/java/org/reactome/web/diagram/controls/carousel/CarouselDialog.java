package org.reactome.web.diagram.controls.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import org.reactome.web.diagram.common.PwpButton;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class CarouselDialog extends PopupPanel {
    private CarouselPanel carousel;
    private List<Slide> slidesList;

    public CarouselDialog(List<Slide> slidesList, int slideWidth, int slideHeight) {
        super();
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);

        this.slidesList = slidesList;
        initUI(slideWidth, slideHeight);
    }

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
    }

    @Override
    public void show() {
        super.show();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                center();
            }
        });
    }

    private void initUI(int slideWidth, int slideHeight) {
        ResourceCSS css = RESOURCES.getCSS();
        setStyleName(css.popupPanel());

        Button closeBtn = new PwpButton("Close this dialog", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                CarouselDialog.this.hide();
            }
        });

        carousel = new CarouselPanel(slidesList, slideWidth, slideHeight);

        FlowPanel mainPanel = new FlowPanel();              // Main panel
        mainPanel.add(closeBtn);
        mainPanel.add(carousel);
        add(mainPanel);
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

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-CarouselDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/carousel/CarouselDialog.css";

        String popupPanel();

        String close();
    }
}
