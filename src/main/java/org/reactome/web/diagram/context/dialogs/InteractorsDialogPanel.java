package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorSelectedEvent;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorSelectedHandler;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorsTable;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.InteractorsErrorEvent;
import org.reactome.web.diagram.events.InteractorsLoadedEvent;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;
import org.reactome.web.diagram.handlers.InteractorsErrorHandler;
import org.reactome.web.diagram.handlers.InteractorsLoadedHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDialogPanel extends Composite implements InteractorSelectedHandler, InteractorsLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsErrorHandler {

    private EventBus eventBus;
    private FlowPanel container;
    private Image loadingIcon;
    private DiagramContext context;
    private GraphPhysicalEntity physicalEntity;

    private AnalysisType analysisType;
    private List<String> expColumns;
    private Double min;
    private Double max;
    private int selectedExpCol = 0;

    private String currentResource;

    private List<RawInteractor> interactions;
    private InteractorsTable<RawInteractor> interactorsTable;

    public InteractorsDialogPanel(EventBus eventBus, DiagramObject diagramObject, DiagramContext context) {
        this.eventBus = eventBus;
        this.context = context;
        this.physicalEntity = diagramObject.getGraphObject();
        this.currentResource = LoaderManager.INTERACTORS_RESOURCE;
        this.interactions = new LinkedList<>();

        initialiseWidget();
        initHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void initHandlers(){
        this.eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    public void forceDraw(){
        if(interactorsTable!=null) {
            interactorsTable.redraw();
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        updateDialogContent();
        showLoading(false);
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        updateDialogContent();
        showLoading(false);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
        showLoading(true);
        updateDialogContent();
    }

    @Override
    public void onInteractorSelected(InteractorSelectedEvent event) {

    }

    private void initialiseWidget(){
        container = new FlowPanel();
        container.setStyleName(RESOURCES.getCSS().container());

        interactorsTable = new InteractorsTable("Interactors", analysisType, expColumns, min, max, selectedExpCol);
        interactorsTable.setHeight("150px");
        interactorsTable.addMoleculeSelectedHandler(this);

        loadingIcon = new Image(PathwaysDialogPanel.RESOURCES.loader());
        container.add(loadingIcon);
        container.add(interactorsTable);
        initWidget(container);

        updateDialogContent();
        showLoading(false);
    }

    private void updateDialogContent(){
        if(context==null) return;
        if(context.getInteractors().isResourceLoaded(currentResource)){
            setInteractions();
        } else {
            showLoading(true);
        }
    }


    private void setInteractions() {
        if (context != null) {
            Set<RawInteractor> aux = context.getInteractors().getRawInteractors(currentResource, physicalEntity.getIdentifier());
            interactions.clear();
            if(aux!=null) {
                interactions.addAll(aux);
            }
            interactorsTable.updateRows(interactions);
            showLoading(false);
        }
    }

    private void showLoading(boolean isLoading){
        loadingIcon.setVisible(isLoading);
        interactorsTable.setVisible(!isLoading);
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

    @CssResource.ImportedWithPrefix("diagram-MoleculesDialogPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/DialogPanelsCommon.css";

        String container();
    }
}
