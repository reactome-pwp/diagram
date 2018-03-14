package org.reactome.web.diagram.search.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ScopeBarPanel extends FlowPanel implements ClickHandler {
    private List<Button> btns = new LinkedList<>();
    private Button activeButton;
    private Handler handler;

    public interface Handler {
        void onScopeChanged(int selected);
    }

    public ScopeBarPanel(Handler handler) {
        this.handler = handler;
        setStyleName(RESOURCES.getCSS().container());
    }

    public void addButton(String text, ImageResource imageResource) {
        Image buttonImg = new Image(imageResource);
        Label buttonLbl = new Label(text);

        FlowPanel fp = new FlowPanel();
        fp.add(buttonImg);
        fp.add(buttonLbl);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        btns.add(btn);
        if (btns.size() == 1) {
            setActiveButton(btn);
        }
        add(btn);
    }

    @Override
    public void onClick(ClickEvent event) {
        Button button = (Button) event.getSource();
        if (button.equals(activeButton)) return;
        setActiveButton(button);

        handler.onScopeChanged(btns.indexOf(button));
    }

    public void setActiveButton(Button button) {
        if (activeButton!=null) activeButton.removeStyleName(RESOURCES.getCSS().buttonSelected());
        button.addStyleName(RESOURCES.getCSS().buttonSelected());
        activeButton = button;
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
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ScopeBarPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/results/ScopeBarPanel.css";

        String container();

        String buttonSelected();
    }
}
