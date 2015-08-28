package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.Console;

import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MoleculesDialogPanel extends Composite {

    private EventBus eventBus;

    public MoleculesDialogPanel(EventBus eventBus, DiagramObject diagramObject, List<String> expColumns) {
        this.eventBus = eventBus;
        GraphObject graphObject = diagramObject.getGraphObject();

        if (graphObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
            Set<GraphPhysicalEntity> participants = pe.getParticipants();
            FlowPanel vp = new FlowPanel();

            FlowPanel proteins = new FlowPanel();
            proteins.add(getSectionTitle("Proteins"));
            FlowPanel chemicals = new FlowPanel();
            chemicals.add(getSectionTitle("Chemical compounds"));
            FlowPanel dnas = new FlowPanel();
            dnas.add(getSectionTitle("DNA"));
            FlowPanel others = new FlowPanel();
            others.add(getSectionTitle("Others"));

            for (final GraphPhysicalEntity participant : participants) {
                FlowPanel table;
                if (participant instanceof GraphSimpleEntity) {
                    table = chemicals;
                } else if (participant instanceof GraphEntityWithAccessionedSequence) {
                    table = proteins;
                } else if (participant instanceof GraphGenomeEncodedEntity) {
                    table = dnas;
                } else {
                    table = others;
                }
                table.setStyleName(RESOURCES.getCSS().sectionTable());

                FlowPanel row = new FlowPanel();
                row.setStyleName(RESOURCES.getCSS().sectionRow());
                table.add(row);

                Label label;
                if (participant.getIdentifier() != null && !participant.getIdentifier().isEmpty()) {
                    label = new Label(participant.getIdentifier());
                    label.setTitle(participant.getDisplayName());
                } else {
                    label = new Label(participant.getDisplayName());
                }
                label.setStyleName(RESOURCES.getCSS().participant());
                label.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Console.info(participant.getDisplayName());
                    }
                });
                row.add(label);

                for (int col = 0; col < expColumns.size(); col++) {
                    Double exp = (participant.getExpression() != null) ? participant.getExpression().get(col) : null;
                    Label expLabel = new Label(exp != null ? "" + exp : "");
                    expLabel.setStyleName(RESOURCES.getCSS().expressionValue());
                    row.add(expLabel);
                }
            }

            //There is a certain order in which we want the participating molecules to be listed
            if (proteins.getWidgetCount() > 1) vp.add(proteins);
            if (chemicals.getWidgetCount() > 1) vp.add(chemicals);
            if (dnas.getWidgetCount() > 1) vp.add(dnas);
            if (others.getWidgetCount() > 1) vp.add(others);

            initWidget(new ScrollPanel(vp));
        } else {
            initWidget(new InlineLabel("???"));
        }
    }

    private Label getSectionTitle(String title) {
        Label label = new Label(title);
        label.setStyleName(RESOURCES.getCSS().sectionTitle());
        return label;
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

        String sectionTitle();

        String sectionTable();

        String sectionRow();

        String participant();

        String expressionValue();
    }
}
