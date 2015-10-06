package org.reactome.web.diagram.controls.top.menu.submenu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AboutDialog extends DialogBox {

    public AboutDialog(TextResource about) {
        super(true, false);
        setStyleName(RESOURCES.getCSS().popup());
        setTitlePanel();
        FlowPanel fp = new FlowPanel();
        fp.getElement().getStyle().setFloat(Style.Float.RIGHT);
        fp.add(new HTMLPanel(about.getText()));
        Button closeBtn = new Button("", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        closeBtn.setTitle("Close the about panel");
        closeBtn.setStyleName(RESOURCES.getCSS().close());
        fp.add(closeBtn);
        add(fp);
    }

    private void setTitlePanel() {
        FlowPanel fp = new FlowPanel();
        Image img = new Image(RESOURCES.headerIcon());
        fp.add(img);

        InlineLabel title = new InlineLabel("About Pathway Diagrams");
        fp.add(title);

        SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(fp.toString());
        getCaption().setHTML(safeHtml);
        getCaption().asWidget().getElement().getStyle().setCursor(Style.Cursor.MOVE);
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

        @Source("../../images/about_header.png")
        ImageResource headerIcon();

        @Source("../../images/close_clicked.png")
        ImageResource closeClicked();

        @Source("../../images/close_disabled.png")
        ImageResource closeDisabled();

        @Source("../../images/close_hovered.png")
        ImageResource closeHovered();

        @Source("../../images/close_normal.png")
        ImageResource closeNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-DiagramAboutDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/top/menu/submenu/AboutDialog.css";

        String popup();

        String header();

        String close();
    }
}
