package org.reactome.web.diagram.context.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.PwpButton;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class StatusReport extends PopupPanel {

    private Image icon;
    private Label titleLb;
    private Label messagesLb;

    public StatusReport() {
        super();
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(false);
        this.setStyleName(RESOURCES.getCSS().popupPanel());

        initUI();
    }

    public void displaySuccessIcon() {
        icon.setResource(RESOURCES.success());
    }

    public void displayWarningIcon() {
        icon.setResource(RESOURCES.successWithWarnings());
    }

    public void displayErrorIcon() {
        icon.setResource(RESOURCES.failure());
    }

    public void setTitle(String title){
        this.titleLb.setText(title);
    }

    public void setMessages(List<String> messages) {
        if(messages!=null && !messages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String message : messages) {
                sb.append("\t- ").append(message).append("\n");
            }
            messagesLb.setText(sb.toString());
        }
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

    private void initUI(){
        icon = new Image(RESOURCES.success());
        titleLb = new Label();
        titleLb.setStyleName(RESOURCES.getCSS().title());
        Button closeBtn = new PwpButton("Close this dialog", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                StatusReport.this.hide();
            }
        });

        messagesLb = new Label();
        messagesLb.setStyleName(RESOURCES.getCSS().messageline());

        FlowPanel messagesInnerFP = new FlowPanel();
        messagesInnerFP.setStyleName(RESOURCES.getCSS().messagesInner());
        messagesInnerFP.add(messagesLb);

        FlowPanel messagesOuterFP = new FlowPanel();
        messagesOuterFP.setStyleName(RESOURCES.getCSS().messagesOuter());
        messagesOuterFP.add(messagesInnerFP);

        FlowPanel container = new FlowPanel();
        container.setStyleName(RESOURCES.getCSS().containerPanel());
        container.add(icon);
        container.add(titleLb);
        container.add(closeBtn);
        container.add(messagesOuterFP);

        add(container);
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

        @Source("images/addNewResources.png")
        ImageResource icon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/success.png")
        ImageResource success();

        @Source("images/successWithWarnings.png")
        ImageResource successWithWarnings();

        @Source("images/failure.png")
        ImageResource failure();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-WarningsReport")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/StatusReport.css";

        String popupPanel();

        String containerPanel();

        String title();

        String messagesOuter();

        String messagesInner();

        String messageline();

        String close();
    }
}
