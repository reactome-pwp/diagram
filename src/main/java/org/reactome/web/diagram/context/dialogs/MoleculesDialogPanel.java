package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.context.dialogs.molecules.MoleculeSelectedEvent;
import org.reactome.web.diagram.context.dialogs.molecules.MoleculeSelectedHandler;
import org.reactome.web.diagram.context.dialogs.molecules.MoleculesTable;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.analysis.ExpressionSummary;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;
import org.reactome.web.diagram.handlers.ExpressionColumnChangedHandler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class MoleculesDialogPanel extends Composite implements AnalysisResultLoadedHandler, AnalysisResetHandler,
        ExpressionColumnChangedHandler, AnalysisProfileChangedHandler, ClickHandler, MoleculeSelectedHandler {

    private EventBus eventBus;
    private GraphObject graphObject;

    private AnalysisType analysisType;
    private List<String> expColumns;
    private Double min;
    private Double max;
    private int selectedExpCol = 0;

    private List<GraphEntityWithAccessionedSequence> proteins = new LinkedList<>();
    private List<GraphSimpleEntity> chemicals = new LinkedList<>();
    private List<GraphGenomeEncodedEntity> dnas = new LinkedList<>();
    private List<GraphOtherEntity> others = new LinkedList<>();

    private MoleculesTable<GraphEntityWithAccessionedSequence> proteinsTable;
    private MoleculesTable<GraphSimpleEntity> chemicalsTable;
    private MoleculesTable<GraphGenomeEncodedEntity> dnasTable;
    private MoleculesTable<GraphOtherEntity> othersTable;

    private Button changeBtn;
    private boolean displayNames;

    public MoleculesDialogPanel(EventBus eventBus, DiagramObject diagramObject, AnalysisStatus analysisStatus) {
        this.eventBus = eventBus;
        this.graphObject = diagramObject.getGraphObject();
        if(analysisStatus!=null){
            this.analysisType = analysisStatus.getAnalysisType();
            ExpressionSummary expressionSummary = analysisStatus.getExpressionSummary();
            if(expressionSummary!=null) {
                this.expColumns = expressionSummary.getColumnNames();
                this.max = expressionSummary.getMax();
                this.min = expressionSummary.getMin();
                this.selectedExpCol = analysisStatus.getColumn();
            }
        }
        if (graphObject instanceof GraphPhysicalEntity || graphObject instanceof GraphReactionLikeEvent) {
            divideParticipants();
            initialiseWidget();
        }else{
            String className = graphObject.getClassName().toLowerCase();
            initWidget(new Label("This " + className +" does not contain any other participating molecules."));
        }
        initHandlers();
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        removeExpressionValues();
        loadAnalysisType();
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        analysisType = event.getType();
        loadAnalysisType();
        ExpressionSummary expressionSummary = event.getExpressionSummary();
        removeExpressionValues();
        if(expressionSummary!=null) {
            expColumns = expressionSummary.getColumnNames();
            min = expressionSummary.getMin();
            max = expressionSummary.getMax();
            loadExpressionValues();
        }
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        loadAnalysisType();
        removeExpressionValues();
        loadExpressionValues();
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if(btn.equals(changeBtn)){
            this.displayNames = !this.displayNames;
        }
        //Apply the right style here
        if(this.displayNames) {
            changeBtn.setStyleName(RESOURCES.getCSS().namesActive());
        }else {
            changeBtn.setStyleName(RESOURCES.getCSS().names());
        }
        changeLabels();
    }

    @Override
    public void onExpressionColumnChanged(final ExpressionColumnChangedEvent e) {
        selectedExpCol = e.getColumn();
        highlightColumn(selectedExpCol);
    }

    @Override
    public void onMoleculeSelected(MoleculeSelectedEvent event) {
        GraphPhysicalEntity object = event.getValue();
        if (object != null) {
            eventBus.fireEventFromSource(new GraphObjectSelectedEvent(object, true), this);
        }
    }

    public void forceDraw(){
        if(proteinsTable!=null) proteinsTable.redraw();
        if(chemicalsTable!=null) chemicalsTable.redraw();
        if(dnasTable!=null) dnasTable.redraw();
        if(othersTable!=null) othersTable.redraw();
    }

    private void changeLabels(){
        if(proteinsTable!=null) proteinsTable.setMoleculesLabels(this.displayNames);
        if(chemicalsTable!=null) chemicalsTable.setMoleculesLabels(this.displayNames);
        if(dnasTable!=null) dnasTable.setMoleculesLabels(this.displayNames);
        if(othersTable!=null) othersTable.setMoleculesLabels(this.displayNames);
    }

    private void loadExpressionValues(){
        if(min==null || max==null) return;
        if(proteinsTable!=null) proteinsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(chemicalsTable!=null) chemicalsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(dnasTable!=null) dnasTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(othersTable!=null) othersTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        highlightColumn(selectedExpCol);
    }

    private void loadAnalysisType(){
        if(proteinsTable!=null) proteinsTable.setAnalysisType(analysisType);
        if(chemicalsTable!=null) chemicalsTable.setAnalysisType(analysisType);
        if(dnasTable!=null) dnasTable.setAnalysisType(analysisType);
        if(othersTable!=null) othersTable.setAnalysisType(analysisType);
    }

    private void removeExpressionValues(){
        if(proteinsTable!=null) proteinsTable.removeExpressionColumns();
        if(chemicalsTable!=null) chemicalsTable.removeExpressionColumns();
        if(dnasTable!=null) dnasTable.removeExpressionColumns();
        if(othersTable!=null) othersTable.removeExpressionColumns();
    }

    private void highlightColumn(int col){
        if(proteinsTable!=null) proteinsTable.highlightExpColumn(col);
        if(chemicalsTable!=null) chemicalsTable.highlightExpColumn(col);
        if(dnasTable!=null) dnasTable.highlightExpColumn(col);
        if(othersTable!=null) othersTable.highlightExpColumn(col);
    }

    private void divideParticipants(){
        Set<GraphPhysicalEntity> participants = new HashSet<>();
        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) this.graphObject;
            participants = pe.getParticipants();
        } else if (graphObject instanceof GraphReactionLikeEvent) {
            GraphReactionLikeEvent rle = (GraphReactionLikeEvent) this.graphObject;
            participants = rle.getParticipants();
        }

        proteins = new LinkedList<>();
        chemicals = new LinkedList<>();
        dnas = new LinkedList<>();
        others = new LinkedList<>();

        for (GraphPhysicalEntity participant : participants) {
            if (participant instanceof GraphSimpleEntity) {
                chemicals.add((GraphSimpleEntity) participant);
            } else if (participant instanceof GraphEntityWithAccessionedSequence) {
                proteins.add((GraphEntityWithAccessionedSequence) participant);
            } else if (participant instanceof GraphGenomeEncodedEntity) {
                dnas.add((GraphGenomeEncodedEntity) participant);
            } else {
                others.add((GraphOtherEntity) participant);
            }
        }
    }

    private void initHandlers(){
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
    }

    private void initialiseWidget(){
        FlowPanel vp = new FlowPanel();
        vp.setStyleName(RESOURCES.getCSS().container());

        changeBtn = new PwpButton("Show/hide names", RESOURCES.getCSS().names(), this);
        vp.add(changeBtn);

        //There is a certain order in which we want the participating molecules to be listed
        if (!proteins.isEmpty()){
            proteinsTable = new MoleculesTable<>("Proteins", proteins, analysisType, expColumns, min, max, selectedExpCol);
            proteinsTable.setHeight(getOptimalSize(proteins) + "px");
            proteinsTable.addMoleculeSelectedHandler(this);
            vp.add(proteinsTable);
        }
        if (!chemicals.isEmpty()) {
            chemicalsTable = new MoleculesTable<>("Chemical compounds", chemicals, analysisType, expColumns, min, max, selectedExpCol);
            chemicalsTable.setHeight(getOptimalSize(chemicals) + "px");
            chemicalsTable.addMoleculeSelectedHandler(this);
            vp.add(chemicalsTable);
        }
        if (!dnas.isEmpty()) {
            dnasTable = new MoleculesTable<>("DNA", dnas, analysisType, expColumns, min, max, selectedExpCol);
            dnasTable.setHeight(getOptimalSize(dnas) + "px");
            dnasTable.addMoleculeSelectedHandler(this);
            vp.add(dnasTable);
        }
        if (!others.isEmpty()){
            othersTable = new MoleculesTable<>("Others", others, analysisType, expColumns, min, max, selectedExpCol);
            othersTable.setHeight(getOptimalSize(others) + "px");
            othersTable.addMoleculeSelectedHandler(this);
            vp.add(othersTable);
        }
        // Hide the button if nothing is shown
        if(proteins.isEmpty() && chemicals.isEmpty() && dnas.isEmpty() && others.isEmpty()){
            changeBtn.setVisible(false);
        }
        initWidget(vp);
    }

    private int getOptimalSize(List list){
        int maxSize;
        int requiredSections = 0;
        if (proteins.size() > 0)    requiredSections++;
        if (chemicals.size() > 0)   requiredSections++;
        if (dnas.size() > 0)        requiredSections++;
        if (others.size() > 0)      requiredSections++;

        switch(requiredSections){
            case 1:
                maxSize = 145;
                break;
            case 2:
                maxSize = 120;
                break;
            default:
                maxSize = 100;
                break;
        }

        int size = (list.size()+1) * 12 + 15; // Setting the size of the table based on the size of its contents
        return size <= maxSize ? size : maxSize;
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/names_clicked.png")
        ImageResource namesClicked();

        @Source("../images/names_normal.png")
        ImageResource namesNormal();

        @Source("../images/names_hovered.png")
        ImageResource namesHovered();
    }

    @CssResource.ImportedWithPrefix("diagram-MoleculesDialogPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/DialogPanelsCommon.css";

        String container();

        String names();

        String namesActive();
    }
}
