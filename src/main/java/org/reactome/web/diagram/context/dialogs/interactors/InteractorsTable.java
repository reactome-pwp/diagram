package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.diagram.data.analysis.AnalysisType;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsTable<T extends RawInteractor> extends DataGrid<T> {
    private ListDataProvider<T> dataProvider;
    private Column<T, String> type;
    private AnalysisType analysisType;
    private String name;

    public InteractorsTable(String name, AnalysisType analysisType, List<String> expression, Double min, Double max, int sel) {
        super(0, (MoleculesTableResource) GWT.create(MoleculesTableResource.class));
        this.name = name;
        this.analysisType = analysisType;
        setAlwaysShowScrollBars(false);
        setEmptyTableWidget(new HTML("No interactors to Display"));

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(this);
        setMoleculesLabels(false); // Show names by default

        // Make the scrollbars invisible
        HeaderPanel panel = (HeaderPanel) this.getWidget();
        CustomScrollPanel scrollPanel = (CustomScrollPanel) panel.getContentWidget();
        scrollPanel.getHorizontalScrollbar().asWidget().getElement().getStyle().setOpacity(0);
    }

    public HandlerRegistration addMoleculeSelectedHandler(InteractorSelectedHandler handler){
        return addHandler(handler, InteractorSelectedEvent.TYPE);
    }

    public void setMoleculesLabels(boolean showIds){
        if(type!=null) { removeColumn(type); }
        type = buildColumnTitle(showIds);
        insertColumn(0, type, name);
//        redraw();
    }

    public void updateRows(List<T> newList){
        dataProvider.getList().clear();
        dataProvider.getList().addAll(newList);
        setVisibleRange(0, newList.size());
        setRowCount(newList.size());
    }

    private List<T> sortMolecules(List<T> molecules, AnalysisType analysisType){
//        Collections.sort(molecules, GraphPhysicalEntity.getIdentifierComparator());
//        List<T> list = new LinkedList<>();
//        List<T> tailList = new LinkedList<>();
//        if(analysisType==null || analysisType == AnalysisType.OVERREPRESENTATION || analysisType==AnalysisType.SPECIES_COMPARISON) {
//            // We have to move all the NOT HIT molecules at the very end
//            for (T molecule : molecules) {
//                if (!molecule.isHit()) tailList.add(molecule);
//                else list.add(molecule);
//            }
//        }else{
//            // We have to move all the molecules without expression at the very end
//            for (T molecule : molecules) {
//                if (molecule.getExpression() == null || molecule.getExpression().isEmpty()) tailList.add(molecule);
//                else list.add(molecule);
//            }
//        }
//        list.addAll(tailList);
//        return list;

        return molecules;
    }

    private Column<T, String> buildColumnTitle(final boolean showIds) {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                if(showIds){
//                    return object.getIdentifier() != null && !object.getIdentifier().isEmpty() ? object.getIdentifier() : object.getStId();
                }else{
//                    return object.getDisplayName();
                }
                return object.getAcc();
            }
        };
        columnTitle.setSortable(true);
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new InteractorSelectedEvent(object));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildColumnExpression(final int col, double min, double max) {
        return new Column<T, String>(new InteractorCell(min, max)) {
            @Override
            public String getValue(T object) {
//                Double exp = (object.getExpression() != null) ? object.getExpression().get(col) : null;
//                return exp != null ? NumberFormat.getFormat("#.##E0").format(exp) : "";
                return "0.0";
            }
        };
    }

    @Override
    public void redraw() {
        super.redraw();
        if(this.analysisType==AnalysisType.OVERREPRESENTATION || this.analysisType==AnalysisType.SPECIES_COMPARISON ) {
//            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
//                @Override
//                public void execute() {
//                    applyORAColour();
//                }
//            });
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

    @ImportedWithPrefix("diagram-InteractorsTableExpressionSelection")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/dialogs/TableSelectionStyle.css";

        String unselectedExpression();

        String selectedExpression();
    }

    public interface MoleculesTableResource extends DataGrid.Resources {

        @ImportedWithPrefix("diagram-MoleculesTable")
        interface MoleculesStyle extends Style {
            String CSS = "org/reactome/web/diagram/context/dialogs/MoleculesTable.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(MoleculesStyle.CSS)
        MoleculesStyle dataGridStyle();
    }
}
