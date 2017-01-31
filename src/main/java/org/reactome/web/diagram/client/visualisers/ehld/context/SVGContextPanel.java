package org.reactome.web.diagram.client.visualisers.ehld.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SVGContextPanel extends DialogBox implements ClickHandler, DatabaseObjectCreatedHandler {
    private static String MESSAGE = "Go to ";

    private EventBus eventBus;
    private Label label;

    private Pathway targetPathway;

    public SVGContextPanel(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
        setAutoHideEnabled(true);
        setModal(false);
        setStyleName(RESOURCES.getCSS().popup());

        this.label = new Label("");
        this.label.addClickHandler(this);
        label.setStyleName(RESOURCES.getCSS().label());

        FlowPanel fp = new FlowPanel();
        fp.add(this.label);

        setWidget(fp);
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(label)) {
            eventBus.fireEventFromSource(new ContentRequestedEvent(targetPathway.getDbId() + ""), this);
        }
        hide();
    }

    @Override
    public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
        if(databaseObject instanceof Pathway) {
            targetPathway = (Pathway) databaseObject;
            label.setText(MESSAGE + targetPathway.getDisplayName() + " (" + targetPathway.getStableIdentifier().getIdentifier() + ")");
            super.show();
        }
    }

    @Override
    public void onDatabaseObjectError(Throwable exception) {
        label.setText("");
        hide();
    }

    public void show(String stableId, int x, int y) {
        if(stableId != null) {
            DatabaseObjectFactory.get(stableId, this);
            setPosition(x, y);
        }
    }

    private void setPosition(int x, int y) {
        //TODO sort out the details of the positioning
        setPopupPosition(x + 5, y + 5);
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

    @CssResource.ImportedWithPrefix("diagram-SVGContextPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/client/SVGContextPanel.css";

        String popup();

        String label();
    }
}
