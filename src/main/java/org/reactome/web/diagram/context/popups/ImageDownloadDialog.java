package org.reactome.web.diagram.context.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.PwpButton;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ImageDownloadDialog extends PopupPanel {

    public ImageDownloadDialog(Image image){
        super();
        String userAgent = Window.Navigator.getUserAgent().toLowerCase();
        boolean isIE = userAgent.contains("msie") || userAgent.contains("trident");
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);
        this.addStyleName(RESOURCES.getCSS().popupPanel());

        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(RESOURCES.getCSS().analysisPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        FlowPanel imagePanel = new FlowPanel();
        imagePanel.add(image);
        imagePanel.setStyleName(RESOURCES.getCSS().imagePanel());
        image.setStyleName(RESOURCES.getCSS().image());
        vp.add(imagePanel);
        this.add(vp);

        if (isIE) {
            InlineLabel infoLabel = new InlineLabel("To save the diagram, simply right-click on the image, and then click \'Save Picture As...\'");
            infoLabel.addStyleName(RESOURCES.getCSS().infoLabel());
            vp.add(infoLabel);
        } else {
            Anchor anchor = new Anchor();                     // For downloading the image
            anchor.setHref(image.getUrl());
            vp.add(anchor);
            setAnchorContent(anchor);
        }
    }

    private Widget setTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        header.add(image);
        Label title = new Label("Export diagram to image");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new PwpButton("Close", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ImageDownloadDialog.this.hide();
            }
        });
        header.add(title);
        header.add(closeBtn);
        return header;
    }

    private void setAnchorContent(final Anchor anchor){
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    anchor.getElement().setAttribute("download", "DiagramImage.png");
                    Button button = new PwpButton("Save diagram as image", RESOURCES.getCSS().downloadLink(), new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            ImageDownloadDialog.this.hide();
                        }
                    });
                    anchor.getElement().appendChild(button.getElement());
                    ImageDownloadDialog.this.center();
                }
            }
        );
    }

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
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

        @Source("images/header_icon.png")
        ImageResource headerIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/download_clicked.png")
        ImageResource downloadClicked();

        @Source("images/download_hovered.png")
        ImageResource downloadHovered();

        @Source("images/download_normal.png")
        ImageResource downloadNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ImageDownloadDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/ImageDownloadDialog.css";

        String popupPanel();

        String analysisPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String imagePanel();

        String image();

        String downloadLink();

        String infoLabel();
    }
}
