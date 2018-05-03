package org.reactome.web.diagram.search.detailspanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import org.reactome.web.diagram.common.IconToggleButton;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.events.DiagramObjectsFlagRequestedEvent;
import org.reactome.web.diagram.search.SearchLauncher;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.results.ResultItem;

/**
 * Creates a title in the DetailsPanel containing various
 * information about the selected item, such as name, accession,
 * type, compartment etc.
 *
 * The view of the title is slightly different depending on whether
 * the displayed item is a {@link ResultItem} or a {@link InteractorSearchResult}
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class TitlePanel extends FlowPanel implements ClickHandler {
    private EventBus eventBus;
    private SearchResultObject selectedItem;

    private Label name;
    private IconToggleButton flagBtn;
//    private Label type;

    private FlowPanel firstLine;

    public TitlePanel(EventBus eventBus, SearchResultObject selectedItem) {
        this.eventBus = eventBus;
        this.selectedItem = selectedItem;

        initialise();

        if (selectedItem instanceof ResultItem) {
            populate((ResultItem) selectedItem);
        } else if (selectedItem instanceof InteractorSearchResult) {
            populate((InteractorSearchResult) selectedItem);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (selectedItem instanceof ResultItem) {
            eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(((ResultItem) selectedItem).getStId()), this);
        } else if (selectedItem instanceof InteractorSearchResult) {
            eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(((InteractorSearchResult) selectedItem).getAccession()), this);
        }

    }

    private void initialise() {
        setStyleName(RESOURCES.getCSS().container());

        name = new Label();
        name.setStyleName(RESOURCES.getCSS().name());

        flagBtn = new IconToggleButton("", SearchLauncher.RESOURCES.clear(), SearchLauncher.RESOURCES.clear());
        flagBtn.setStyleName(RESOURCES.getCSS().flagBtn());
        flagBtn.setVisible(true);
        flagBtn.setTitle("Show where this is in the diagram");
        flagBtn.addClickHandler(this);

        firstLine = new FlowPanel();
        firstLine.setStyleName(RESOURCES.getCSS().line());

        add(flagBtn);
        add(name);
        add(firstLine);
    }

    private void populate(ResultItem item) {
        name.setText(item.getName());
        name.setTitle(item.getName());

        createAndAddLabel(item.getSchemaClass().name, "Type", RESOURCES.getCSS().type(), firstLine);
        createAndAddLabel(item.getStId(), "Id", RESOURCES.getCSS().id(), firstLine);
        createAndAddLabel(item.getReferenceIdentifier(), item.getDatabaseName() + ":" + item.getReferenceIdentifier(), RESOURCES.getCSS().accession(), firstLine);
        createAndAddLabel(item.getCompartments(), "Compartments", RESOURCES.getCSS().compartments(), firstLine);
        createAndAddLabel("Gene names", "Gene names", RESOURCES.getCSS().genes(), firstLine);
    }

    private void populate(InteractorSearchResult item) {
        String alias = item.getAlias();
        if (alias != null) {
            name.setText(alias);
            name.setTitle(alias);

            createAndAddLabel(item.getAccession(), "Accession", RESOURCES.getCSS().accession(), firstLine);
        } else {
            name.setText(item.getAccession());
            name.setTitle(item.getAccession());
        }

        //TODO check why the following is not working
//        addLabelFor(item.getSchemaClass().name, "Type", RESOURCES.getCSS().type(), firstLine);
        createAndAddLabel("Interactor", "Type", RESOURCES.getCSS().type(), firstLine);
        createAndAddLabel(item.getResource().getName(), "Resource", RESOURCES.getCSS().id(), firstLine);
    }

    private void createAndAddLabel(String text, String tooltip, String style, Panel container) {
        if(text != null && !text.isEmpty()) {
            Label label = new Label();
            label.setStyleName(style);
            label.setText(text);
            label.setTitle(tooltip);
            container.add(label);
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
    }

    @CssResource.ImportedWithPrefix("diagram-TitlePanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/search/detailspanel/TitlePanel.css";

        String container();

        String name();

        String line();

        String id();

        String compartments();

        String type();

        String genes();

        String accession();

        String flagBtn();
    }
}
