package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite implements ValueChangeHandler, SelectionChangeEvent.Handler {
    private EventBus eventBus;
    private RadioButton staticResourceBtn;
    private RadioButton liveResourceBtn;

    private CellList<ResourceObject> liveResourcesList;
    private final SingleSelectionModel<ResourceObject> selectionModel;
    private ListDataProvider<ResourceObject> dataProvider;

    /**
     * The key provider that provides the unique ID of a interactor resource.
     */
    public static final ProvidesKey<ResourceObject> KEY_PROVIDER = new ProvidesKey<ResourceObject>() {
        @Override
        public Object getKey(ResourceObject item) {
            return item == null ? null : item.getId();
        }
    };

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        staticResourceBtn = new RadioButton("Resources", "IntAct static");
        staticResourceBtn.setTitle("Select IntAct as a resource");
        staticResourceBtn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
        staticResourceBtn.setValue(true);
        staticResourceBtn.addValueChangeHandler(this);

        liveResourceBtn = new RadioButton("Resources", "PSICQUIC");
        liveResourceBtn.setTitle("Select one of the PSICQUIC resources");
        liveResourceBtn.setStyleName(RESOURCES.getCSS().interactorResourceBtn());
        liveResourceBtn.addValueChangeHandler(this);

        // Add a selection model so we can select cells.
        selectionModel = new SingleSelectionModel<>(KEY_PROVIDER);
        selectionModel.addSelectionChangeHandler(this);

        ResourceCell resourceCell = new ResourceCell();
        liveResourcesList = new CellList<>(resourceCell, GWT.<CustomCellListResources>create(CustomCellListResources.class),KEY_PROVIDER);
        liveResourcesList.sinkEvents(Event.FOCUSEVENTS);
        liveResourcesList.setSelectionModel(selectionModel);
        liveResourcesList.setStyleName(RESOURCES.getCSS().interactorResourceList());

        liveResourcesList.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        liveResourcesList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        liveResourcesList.setVisible(false);
        setResourcesList(Arrays.asList( "Resource2", "Resource3", "Resource4", "Resource5", "Resource6"));

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        main.add(staticResourceBtn);
        main.add(liveResourceBtn);
        main.add(liveResourcesList);
        initWidget(main);
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        if (selectedBtn.equals(staticResourceBtn)) {
            liveResourcesList.setVisible(false);
            // Fire event for static Resource selection
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent("Resource1"), this);
        } else if (selectedBtn.equals(liveResourceBtn)) {
            // Show the list of live resources select the one previously selected
            ResourceObject obj = selectionModel.getSelectedObject();
            if(obj != null){
                selectionModel.setSelected(obj,true);
                eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(selectionModel.getSelectedObject().getName()), this);
            }
            liveResourcesList.setVisible(true);
        }
    }

    private void setResourcesList(List<String> resourcesList){
        List<ResourceObject> aux = new ArrayList<>();
        for(int i=0; i<resourcesList.size(); i++) {
            aux.add(new ResourceObject(i, resourcesList.get(i), i == 0));
        }
        populateResourceList(aux);
    }

    private void populateResourceList(List<ResourceObject> resourcesList){
        if(!resourcesList.isEmpty()){
            selectionModel.clear();
        }
        dataProvider = new ListDataProvider<>(resourcesList);
        dataProvider.addDataDisplay(this.liveResourcesList);
    }

    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
        ResourceObject obj = selectionModel.getSelectedObject();
        if(obj!=null) {
            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(obj.getName()), this);
        }
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/active.gif")
        ImageResource active();

        @Source("../images/inactive.png")
        ImageResource inActive();
    }

    interface CustomCellListResources extends CellList.Resources {
        // Used for custom styling of the CellList
        @Override
        @Source({"org/reactome/web/diagram/controls/settings/tabs/CustomCellList.css"})
        CellList.Style cellListStyle();
    }


    @CssResource.ImportedWithPrefix("diagram-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String interactorResourceBtn();

        String tabHeader();

        String interactorResourceList();
    }
}
