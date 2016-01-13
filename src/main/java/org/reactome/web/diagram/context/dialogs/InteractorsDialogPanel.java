package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorSelectedEvent;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorSelectedHandler;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorsTable;
import org.reactome.web.diagram.data.DiagramContext;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDialogPanel extends Composite implements  AnalysisResultLoadedHandler, AnalysisResetHandler,
        ExpressionColumnChangedHandler, AnalysisProfileChangedHandler, InteractorSelectedHandler, InteractorsResourceChangedHandler {

    private EventBus eventBus;
    private DiagramContext context;
    private GraphPhysicalEntity physicalEntity;

    private AnalysisType analysisType;
    private List<String> expColumns;
    private Double min;
    private Double max;
    private int selectedExpCol = 0;

    private String currentResource;

    private Set<RawInteractor> interactions;
    private InteractorsTable<RawInteractor> interactorsTable;

    public InteractorsDialogPanel(EventBus eventBus, DiagramObject diagramObject, DiagramContext context) {
        this.eventBus = eventBus;
        this.context = context;
        this.physicalEntity = diagramObject.getGraphObject();
        this.currentResource = LoaderManager.INTERACTORS_RESOURCE;
        setInteractions();
        initialiseWidget();
        initHandlers();
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {

    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {

    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {

    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {

    }

    @Override
    public void onInteractorSelected(InteractorSelectedEvent event) {

    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource();
    }

    private void setInteractions() {
        if (context != null) {
            interactions = context.getInteractors().getRawInteractors(currentResource, physicalEntity.getIdentifier());
        }
    }

    private void initHandlers(){
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
    }

    private void initialiseWidget(){
        FlowPanel vp = new FlowPanel();
        vp.setStyleName(RESOURCES.getCSS().container());

        //There is a certain order in which we want the participating molecules to be listed
        if (!interactions.isEmpty()){
            interactorsTable = new InteractorsTable("Interactors", new LinkedList(interactions), analysisType, expColumns, min, max, selectedExpCol);
            interactorsTable.setHeight("150px");
            interactorsTable.addMoleculeSelectedHandler(this);
            vp.add(interactorsTable);
        }
        initWidget(vp);
    }

    public void forceDraw(){
        if(interactorsTable!=null) interactorsTable.redraw();
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
