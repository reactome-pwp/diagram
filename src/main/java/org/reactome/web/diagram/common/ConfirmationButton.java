package org.reactome.web.diagram.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ConfirmationButton extends Composite implements ClickHandler{

    private FlowPanel container;
    private FlowPanel innerContainer;
    private IconButton normalButton;
    private IconButton okButton;
    private IconButton cancelButton;

    private String title;
    private ImageResource icon;
    private ClickHandler clickHandler;


    public ConfirmationButton(String title, ImageResource icon, ClickHandler clickHandler) {
        this.title = title;
        this.icon = icon;
        this.clickHandler = clickHandler;
        init();
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(normalButton)) {
            expand();
        } else {
            collapse();
        }
    }

    private void init() {
        normalButton = new IconButton("", icon);
        normalButton.addClickHandler(this);
        normalButton.setTitle(title);
        normalButton.setStyleName(RESOURCES.getCSS().normalBtn());

        okButton = new IconButton("", RESOURCES.okIcon());
        okButton.addClickHandler(clickHandler);
        okButton.setTitle("Proceed");
        okButton.setStyleName(RESOURCES.getCSS().okBtn());

        cancelButton = new IconButton("", RESOURCES.cancelIcon());
        cancelButton.addClickHandler(this);
        cancelButton.setTitle("Cancel");
        cancelButton.setStyleName(RESOURCES.getCSS().cancelBtn());

        innerContainer = new FlowPanel();
        innerContainer.setStyleName(RESOURCES.getCSS().innerContainer());
        innerContainer.add(normalButton);
        innerContainer.add(okButton);
        innerContainer.add(cancelButton);

        container = new FlowPanel();
        container.setStyleName(RESOURCES.getCSS().container());
        container.add(innerContainer);
        this.initWidget(container);

        //Set it collapsed originally
        collapse();
    }

    private void expand() {
        container.getElement().getStyle().setWidth(40, Style.Unit.PX);
        innerContainer.getElement().getStyle().setMarginLeft(-19, Style.Unit.PX);
    }

    private void collapse(){
        container.getElement().getStyle().setWidth(19, Style.Unit.PX);
        innerContainer.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/ok.png")
        ImageResource okIcon();

        @Source("images/cancel.png")
        ImageResource cancelIcon();

    }

    @CssResource.ImportedWithPrefix("diagram-ConfirmationButton")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/common/ConfirmationButton.css";

        String container();

        String innerContainer();

        String normalBtn();

        String okBtn();

        String cancelBtn();
    }
}
