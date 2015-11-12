package org.reactome.web.diagram.controls.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.controls.settings.tabs.AboutTabPanel;
import org.reactome.web.diagram.controls.settings.tabs.InteractorsTabPanel;
import org.reactome.web.diagram.controls.settings.tabs.ProfilesTabPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class HideableContainerPanel extends FlowPanel implements ClickHandler {
    private boolean isExpanded = false;
    private List<Button> btns = new LinkedList<>();
    private Button profilesBtn;
    private Button interactorsBtn;
    private Button aboutBtn;
    private PwpButton showBtn;

    private DeckLayoutPanel container;

    public HideableContainerPanel(EventBus eventBus) {
        setStyleName(RESOURCES.getCSS().wrapper());

        FlowPanel buttonsPanel = new FlowPanel();               // Tab buttons panel
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.addStyleName(RESOURCES.getCSS().unselectable());
        buttonsPanel.add(this.profilesBtn = getButton("Colour Profiles", RESOURCES.profilesTabSmall()));
        buttonsPanel.add(this.interactorsBtn = getButton("Interactors", RESOURCES.interactorsTabSmall()));
        buttonsPanel.add(this.aboutBtn = getButton("About Reactome", RESOURCES.aboutTabSmall()));
        this.profilesBtn.addStyleName(RESOURCES.getCSS().buttonSelected());

        this.container = new DeckLayoutPanel();                 // Main tab container
        this.container.setStyleName(RESOURCES.getCSS().container());

        ProfilesTabPanel profilesTabPanel = new ProfilesTabPanel(eventBus);
        InteractorsTabPanel interactorsTabPanel= new InteractorsTabPanel(eventBus);
        AboutTabPanel aboutTabPanel = new AboutTabPanel("About the Pathway Diagram",RESOURCES.aboutThis());
        this.container.add(profilesTabPanel);
        this.container.add(interactorsTabPanel);
        this.container.add(aboutTabPanel);

        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);

        FlowPanel outerPanel = new FlowPanel();                 // Vertical tab Panel and buttons container
        outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
        outerPanel.add(buttonsPanel);
        outerPanel.add(this.container);

        showBtn = new PwpButton("Show/Hide settings", RESOURCES.getCSS().showHide(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
               HideableContainerPanel.this.toggle();
            }
        });

        InlineLabel header = new InlineLabel("Settings");
        header.setStyleName(RESOURCES.getCSS().headerLabel());
        FlowPanel mainPanel = new FlowPanel();                         // Main panel
        mainPanel.add(showBtn);
        mainPanel.add(header);
        mainPanel.add(outerPanel);
        add(mainPanel);
    }

    private void collapse(){
        if(isExpanded) {
            removeStyleName(RESOURCES.getCSS().wrapperExpanded());
            showBtn.removeStyleName(RESOURCES.getCSS().showHideRight());
            isExpanded = false;
        }
    }

    private void expand(){
        if(!isExpanded) {
            addStyleName(RESOURCES.getCSS().wrapperExpanded());
            showBtn.addStyleName(RESOURCES.getCSS().showHideRight());
            isExpanded = true;
        }
    }

    private void toggle(){
        if(!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    public Button getButton(String text, ImageResource imageResource){
        FlowPanel fp = new FlowPanel();
        Image image = new Image(imageResource);
        image.addStyleName(RESOURCES.getCSS().undraggable());
        fp.add(image);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
        btn.setTitle(text);
        this.btns.add(btn);
        return btn;
    }

    @Override
    public void onClick(ClickEvent event) {
        for (Button btn : btns) {
            btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
        }
        Button btn = (Button) event.getSource();
        btn.addStyleName(RESOURCES.getCSS().buttonSelected());
        expand();
        if(btn.equals(this.profilesBtn)){
            this.container.showWidget(0);
        }else if(btn.equals(this.interactorsBtn)){
            this.container.showWidget(1);
        }else if(btn.equals(this.aboutBtn)){
            this.container.showWidget(2);
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

        @Source("tabs/aboutDiagram.html")
        TextResource aboutThis();

        @Source("images/profiles.png")
        ImageResource profilesTab();

        @Source("images/profiles_s.png")
        ImageResource profilesTabSmall();

        @Source("images/interactors.png")
        ImageResource interactorsTab();

        @Source("images/interactors_s.png")
        ImageResource interactorsTabSmall();

        @Source("images/about.png")
        ImageResource aboutTab();

        @Source("images/about_s.png")
        ImageResource aboutTabSmall();

        @Source("images/showHide.png")
        ImageResource showHideIcon();

    }

    @CssResource.ImportedWithPrefix("diagram-HideableContainerPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/HideableContainerPanel.css";

        String wrapper();

        String wrapperExpanded();

        String outerPanel();

        String buttonsPanel();

        String buttonSelected();

        String unselectable();

        String undraggable();

        String container();

        String showHide();

        String showHideRight();

        String headerLabel();

    }
}
