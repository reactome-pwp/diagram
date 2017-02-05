package org.reactome.web.diagram.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ExpandibleContainer extends AbsolutePanel {
    private SimplePanel primaryButton;
    private Set<Button> buttons;

    public ExpandibleContainer(String tooltip, String style) {
        setStyleName(RESOURCES.getCSS().container());
        this.primaryButton = new SimplePanel();
        this.primaryButton.setTitle(tooltip);
        this.primaryButton.addStyleName(style);
        this.primaryButton.addStyleName(RESOURCES.getCSS().baseButtons());
        this.primaryButton.addStyleName(RESOURCES.getCSS().primaryButton());
        this.add(primaryButton);

        buttons = new HashSet<>();
    }

    public void addButton(Button button) {
        button.addStyleName(RESOURCES.getCSS().baseButtons());
        button.addStyleName(RESOURCES.getCSS().optionButtons());
        buttons.add(button);
        add(button);
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

    @CssResource.ImportedWithPrefix("diagram-ExpandibleButton")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/common/ExpandibleContainer.css";

        String container();

        String baseButtons();

        String optionButtons();

        String primaryButton();

    }
}
