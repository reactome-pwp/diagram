package org.reactome.web.diagram.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContextDialogPanel extends DialogBox implements ClickHandler {

    private DiagramObject item;

    private boolean pinned = false;
    private Button pin;
    private Button close;

    public ContextDialogPanel(EventBus eventBus, DiagramObject item, DiagramContext context) {
        super();
        setAutoHideEnabled(true);
        setModal(false);
        setStyleName(RESOURCES.getCSS().popup());

        this.item = item;

        FlowPanel fp = new FlowPanel();
        fp.add(this.pin = new PwpButton("Keeps the panel visible", RESOURCES.getCSS().pin(), this));
        fp.add(this.close = new PwpButton("Close", RESOURCES.getCSS().close(), this));
        fp.add(new ContextInfoPanel(eventBus, item, context));

        setTitlePanel();
        setWidget(fp);

        center();
        show();
    }

    @Override
    public void hide(boolean autoClosed) {
        //The idea is to keep the panels open if the pin is pressed
        if(autoClosed && !this.pinned) {
            //noinspection ConstantConditions
            super.hide(autoClosed);
        //Or close them in any case when the hide is not automatically triggered ;)
        }else if(!autoClosed){
            //noinspection ConstantConditions
            super.hide(autoClosed);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if(btn.equals(close)){
            this.pinned = false;
            hide();
        }else if(btn.equals(pin)){
            this.pinned = !this.pinned;
        }
        //Apply the right style here
        if(this.pinned) {
            pin.setStyleName(RESOURCES.getCSS().pinActive());
        }else {
            pin.setStyleName(RESOURCES.getCSS().pin());
        }
    }

    public void restore(){
        if(this.pinned) this.show();
    }

    private void setTitlePanel() {
        FlowPanel fp = new FlowPanel();
        Image img = new Image(this.item.getGraphObject().getImageResource());
        fp.add(img);

        InlineLabel title = new InlineLabel(this.item.getDisplayName());
        title.setTitle(this.item.getDisplayName());
        fp.add(title);

        SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(fp.toString());
        getCaption().setHTML(safeHtml);
        getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();


        @Source("images/pin_clicked.png")
        ImageResource pinClicked();

        @Source("images/pin_hovered.png")
        ImageResource pinHovered();

        @Source("images/pin_normal.png")
        ImageResource pinNormal();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

    }

    @CssResource.ImportedWithPrefix("diagram-ContextPopupPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/ContextDialogPanel.css";

        String popup();

        String header();

        String pin();

        String pinActive();

        String close();
    }
}
