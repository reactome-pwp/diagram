package org.reactome.web.diagram.context.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.PwpButton;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ImageDownloadDialog extends PopupPanel {

    public ImageDownloadDialog(final Image image, final String imageFormat, final String diagramStId){
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

        FlowPanel buttons = new FlowPanel();
        if (isIE) {
            Label infoLabel = new Label("To save the image, simply right-click on the image, and then click \'Save Picture As...\'");
            infoLabel.addStyleName(RESOURCES.getCSS().infoLabel());
            buttons.add(infoLabel);
        } else {
            Anchor anchor = new Anchor();                     // For downloading the image
            anchor.setHref(image.getUrl());
            anchor.getElement().setAttribute("download", diagramStId + "." + imageFormat);
            Button button = new IconButton("Download as " + imageFormat.toUpperCase(), RESOURCES.downloadNormal());
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    hide();
                }
            });
            button.setStyleName(RESOURCES.getCSS().downloadPNG());
            button.setTitle("Save diagram as " + imageFormat.toUpperCase() + " image");
            anchor.getElement().appendChild(button.getElement());
            buttons.add(anchor);
        }
        if(gsUploadByPostAvailable()){
            Button genomespace = new IconButton("Upload to GenomeSpace", RESOURCES.uploadNormal());
            genomespace.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    String mimeString = "image/" + imageFormat;
                    String dataURL = image.getUrl();
                    String base64 = dataURL.split(",")[1];
                    // Convert the base64 string to a blob and send to
                    // GenomeSpace using their JavaScript API
                    uploadToGenomeSpace(base64, mimeString, imageFormat, diagramStId);
                    hide();
                }
            });
            genomespace.setStyleName(RESOURCES.getCSS().genomespace());
            genomespace.setTitle("Upload image to GenomeSpace");
            buttons.add(genomespace);
        }
        vp.add(buttons);
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

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
    }

    public void showCentered() {
        super.center();
        setVisible(false);
        // This is necessary to center the panel in Firefox, where getOffsetWidth() and getOffsetHeight()
        // do not return the correct values the first time they are called (even in a deferred call)
        new Timer() {
            @Override
            public void run() {
                int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
                int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
                setPopupPosition(Math.max(Window.getScrollLeft() + left, 0),
                        Math.max(Window.getScrollTop() + top, 0));
                setVisible(true);
            }
        }.schedule(20);
    }

    private static native boolean gsUploadByPostAvailable() /*-{
        return $wnd.gsUploadByPost;
    }-*/;

    private static native void uploadToGenomeSpace(String base64, String mimeString, String imageFormat, String identifier) /*-{
        if(!$wnd.gsUploadByPost) return;
        var binary = atob(base64);                 //
        //noinspection JSPrimitiveTypeWrapperUsage
        var array  = new Array();                  // Adapted from
        for(var i = 0; i < binary.length; i++) {   // stackoverflow.com/questions/4998908
            array.push(binary.charCodeAt(i));      // and many similar discussions
        }                                          //
        var uarray = new Uint8Array(array);        //
        var blob = new Blob([uarray], {type: mimeString});
        var formData = new FormData();
        var imageName = "Reactome_pathway_" + identifier + "." + imageFormat;
        formData.append("webmasterfile", blob, imageName);
        $wnd.gsUploadByPost(formData);
    }-*/;


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

        @Source("images/download_normal.png")
        ImageResource downloadNormal();

        @Source("images/upload_normal.png")
        ImageResource uploadNormal();
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

        String downloadPNG();

        String infoLabel();

        String genomespace();
    }
}
