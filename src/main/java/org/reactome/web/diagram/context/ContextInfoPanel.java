package org.reactome.web.diagram.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.dialogs.MoleculesDialogPanel;
import org.reactome.web.diagram.context.dialogs.PathwaysDialogPanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContextInfoPanel extends Composite implements ClickHandler {

    private List<Button> btns = new LinkedList<>();
    private Button molecules;
    private Button pathways;
    private Button interactors;

    private DeckLayoutPanel container;

    public ContextInfoPanel() {
        FlowPanel buttonsPanel = new FlowPanel();
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.add(this.molecules = getButton("Molecules", RESOURCES.molecules()));
        buttonsPanel.add(this.pathways = getButton("Pathways", RESOURCES.pathways()));
        buttonsPanel.add(this.interactors = getButton("Interactors", RESOURCES.interactors()));
        this.molecules.addStyleName(RESOURCES.getCSS().buttonSelected());

        this.container = new DeckLayoutPanel();
        this.container.setStyleName(RESOURCES.getCSS().container());
        this.container.add(new MoleculesDialogPanel());
        this.container.add(new PathwaysDialogPanel());
        this.container.add(new PathwaysDialogPanel());
        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);

        FlowPanel outerPanel = new FlowPanel();
        outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
        outerPanel.add(buttonsPanel);
        outerPanel.add(this.container);

        initWidget(outerPanel);
    }

    public Button getButton(String text, ImageResource imageResource){
        FlowPanel fp = new FlowPanel();
        fp.add(new Image(imageResource));
        fp.add(new Label(text));

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml, this);
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
        if(btn.equals(this.molecules)){
            this.container.showWidget(0);
        }else if(btn.equals(this.pathways)){
            this.container.showWidget(1);
        }else if(btn.equals(this.interactors)){
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

        @Source("images/interactors.png")
        ImageResource interactors();

        @Source("images/molecules.png")
        ImageResource molecules();

        @Source("images/pathways.png")
        ImageResource pathways();
    }

    @CssResource.ImportedWithPrefix("diagram-ContextInfoPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/context/ContextInfoPanel.css";

        String outerPanel();

        String buttonsPanel();

        String buttonSelected();

        String container();

        String contentPanel();
    }
}
