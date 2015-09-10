package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.context.sections.Section;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MoleculesDialogPanel extends Composite implements AnalysisResultLoadedHandler, AnalysisResetHandler,
        ExpressionColumnChangedHandler, AnalysisProfileChangedHandler {

    private EventBus eventBus;
    private DiagramObject diagramObject;
    private GraphObject graphObject;

    private List<String> expColumns;
    private Double min;
    private Double max;
    private List<String> colNames = new LinkedList<>();

    private List<List<String>> proteins = new LinkedList<>();
    private List<List<String>> chemicals = new LinkedList<>();
    private List<List<String>> dnas = new LinkedList<>();
    private List<List<String>> others = new LinkedList<>();

    private Section proteinsSection;
    private Section chemicalsSection;
    private Section dnasSection;
    private Section othersSection;

    public MoleculesDialogPanel(EventBus eventBus, DiagramObject diagramObject, List<String> expColumns) {
        this.eventBus = eventBus;
        this.diagramObject = diagramObject;
        this.graphObject = diagramObject.getGraphObject();
        this.expColumns = expColumns;
        if (graphObject instanceof GraphPhysicalEntity) {
            divideParticipants();
            initialiseWidget();
            populateTables();
        }else{
            initWidget(new InlineLabel("???")); //TODO: Implement this case
        }
        initHandlers();
    }

    private void divideParticipants(){
        GraphPhysicalEntity pe = (GraphPhysicalEntity) this.graphObject;
        Set<GraphPhysicalEntity> participants = pe.getParticipants();

        proteins = new LinkedList<>();
        chemicals = new LinkedList<>();
        dnas = new LinkedList<>();
        others = new LinkedList<>();

        for (GraphPhysicalEntity participant : participants) {
            List<List<String>> table;
            if (participant instanceof GraphSimpleEntity) {
                table = chemicals;
            } else if (participant instanceof GraphEntityWithAccessionedSequence) {
                table = proteins;
            } else if (participant instanceof GraphGenomeEncodedEntity) {
                table = dnas;
            } else {
                table = others;
            }

            List<String> row = new LinkedList<>();
            String participantName;
            if (participant.getIdentifier() != null && !participant.getIdentifier().isEmpty()) {
                participantName = participant.getIdentifier();
            } else {
                participantName = participant.getDisplayName();
            }
            row.add(participantName);

            if(expColumns!=null && !expColumns.isEmpty()) {
                for (int col = 0; col < expColumns.size(); col++) {
                    Double exp = (participant.getExpression() != null) ? participant.getExpression().get(col) : null;
                    String expression = exp != null ? "" + exp : "";
                    row.add(expression);
                }
            }
            table.add(row);
        }

        colNames.clear();
        if(expColumns!=null && !expColumns.isEmpty()) {
            for (int col = 0; col < expColumns.size(); col++) {
                colNames.add(expColumns.get(col));
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
        int optimalSize = getOptimalSize();
        //There is a certain order in which we want the participating molecules to be listed
        if (proteins.size() > 0){
            proteinsSection = new Section("Proteins", optimalSize);
            vp.add(proteinsSection);
        }
        if (chemicals.size() > 0) {
            chemicalsSection = new Section("Chemical compounds", optimalSize);
            vp.add(chemicalsSection);
        }
        if (dnas.size() > 0) {
            dnasSection = new Section("DNA", optimalSize);
            vp.add(dnasSection);
        }
        if (others.size() > 0){
            othersSection = new Section("Others", optimalSize);
            vp.add(othersSection);
        }
        initWidget(vp);
    }

    private int getOptimalSize(){
        int size;
        int requiredSections = 0;
        if (proteins.size() > 0){   requiredSections++; }
        if (chemicals.size() > 0) { requiredSections++; }
        if (dnas.size() > 0) {      requiredSections++; }
        if (others.size() > 0){     requiredSections++; }

        switch(requiredSections){
            case 1:
                size = 115;
                break;
            case 2:
                size = 50;
                break;
            default:
                size = 40;
                break;
        }

        return size;
    }

    public void populateTables(){
        if (proteins.size() > 0){
            proteinsSection.setTableContents(proteins);
            proteinsSection.setTableHeader(colNames);
            if(expColumns!=null && !expColumns.isEmpty()) {
//                proteinsSection.applyAnalysisColours(proteins, min, max);
//                proteinsSection.selectExpressionCol(0);
            }
        }
        if (chemicals.size() > 0) {
            chemicalsSection.setTableContents(chemicals);
            chemicalsSection.setTableHeader(colNames);
        }
        if (dnas.size() > 0) {
            dnasSection.setTableContents(dnas);
            dnasSection.setTableHeader(colNames);
        }
        if (others.size() > 0){
            othersSection.setTableContents(others);
            othersSection.setTableHeader(colNames);
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        expColumns = null;
        min = null;
        max = null;
        divideParticipants();
        populateTables();
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        ExpressionSummary expressionSummary = event.getExpressionSummary();
        if(expressionSummary!=null) {
            expColumns = expressionSummary.getColumnNames();
            min = expressionSummary.getMin();
            max = expressionSummary.getMax();
            divideParticipants();
            populateTables();

        }
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        ///TODO Implement it
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        proteinsSection.selectExpressionCol(e.getColumn());
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
