package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.context.dialogs.molecules.MoleculesTable;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.analysis.ExpressionSummary;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.AnalysisResetEvent;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.ExpressionColumnChangedEvent;
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
        ExpressionColumnChangedHandler, AnalysisProfileChangedHandler {

    private EventBus eventBus;
    private GraphObject graphObject;

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

    public MoleculesDialogPanel(EventBus eventBus, DiagramObject diagramObject, AnalysisStatus analysisStatus) {
        this.eventBus = eventBus;
        this.graphObject = diagramObject.getGraphObject();
        if(analysisStatus!=null){
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
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
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
        removeExpressionValues();
        loadExpressionValues();
    }

    @Override
    public void onExpressionColumnChanged(final ExpressionColumnChangedEvent e) {
        selectedExpCol = e.getColumn();
        highlightColumn(selectedExpCol);
    }

    public void forceDraw(){
        proteinsTable.redraw();
        chemicalsTable.redraw();
        dnasTable.redraw();
        othersTable.redraw();
    }

    private void loadExpressionValues(){
        if(min==null || max==null) return;
        if(proteinsTable!=null) proteinsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(chemicalsTable!=null) chemicalsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(dnasTable!=null) dnasTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        if(othersTable!=null) othersTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        highlightColumn(selectedExpCol);
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
        String optimalSize = getOptimalSize() + "px";
        //There is a certain order in which we want the participating molecules to be listed
        if (!proteins.isEmpty()){
            proteinsTable = new MoleculesTable<>("Proteins", proteins, expColumns, min, max, selectedExpCol);
            proteinsTable.setHeight(optimalSize);
            vp.add(proteinsTable);
        }
        if (!chemicals.isEmpty()) {
            chemicalsTable = new MoleculesTable<>("Chemical compounds", chemicals, expColumns, min, max, selectedExpCol);
            chemicalsTable.setHeight(optimalSize);
            vp.add(chemicalsTable);
        }
        if (!dnas.isEmpty()) {
            dnasTable = new MoleculesTable<>("DNA", dnas, expColumns, min, max, selectedExpCol);
            dnasTable.setHeight(optimalSize);
            vp.add(dnasTable);
        }
        if (!others.isEmpty()){
            othersTable = new MoleculesTable<>("Others", others, expColumns, min, max, selectedExpCol);
            othersTable.setHeight(optimalSize);
            vp.add(othersTable);
        }
        initWidget(vp);
    }

    private int getOptimalSize(){
        int size;
        int requiredSections = 0;
        if (proteins.size() > 0)    requiredSections++;
        if (chemicals.size() > 0)   requiredSections++;
        if (dnas.size() > 0)        requiredSections++;
        if (others.size() > 0)      requiredSections++;

        switch(requiredSections){
            case 1:
                size = 145;
                break;
            case 2:
                size = 120;
                break;
            default:
                size = 100;
                break;
        }

        return size;
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
