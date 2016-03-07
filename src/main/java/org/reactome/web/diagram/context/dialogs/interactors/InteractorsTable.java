package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CustomScrollPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
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

    private T hoveredItem;
    private double threshold;
    private static String  HIGHLIGHT_COLOUR = "#FFFF00";

    public InteractorsTable(String name, double threshold, AnalysisType analysisType, List<String> expression, Double min, Double max, int sel) {
        super(0, (MoleculesTableResource) GWT.create(MoleculesTableResource.class));
        this.name = name;
        this.analysisType = analysisType;
        this.threshold = threshold;
        setAlwaysShowScrollBars(false);
        setEmptyTableWidget(new HTML("No interactors to display"));

        dataProvider = new ListDataProvider<>();
        dataProvider.addDataDisplay(this);
        setInteractorsLabels(false); // Show names by default

        // Make the scrollbars invisible
        HeaderPanel panel = (HeaderPanel) this.getWidget();
        CustomScrollPanel scrollPanel = (CustomScrollPanel) panel.getContentWidget();
        scrollPanel.getHorizontalScrollbar().asWidget().getElement().getStyle().setOpacity(0);
    }

    public HandlerRegistration addTableItemSelectedHandler(TableItemSelectedHandler handler){
        return addHandler(handler, TableItemSelectedEvent.TYPE);
    }

    public void setInteractorsLabels(boolean showIds){
        if(type!=null) { removeColumn(type); }
        type = buildColumnTitle(showIds);
        insertColumn(0, type, name);

        //We need to add the extra columns only once
        if(getColumnCount() == 1) {
            insertColumn(1, buildEvidencesColumn(), "#Evidences");
            insertColumn(2, buildScoreColumn(), "Score");

            this.setColumnWidth(0, 120, Unit.PX);
            this.setColumnWidth(2, 50, Unit.PX);
        }
    }

    public void updateRows(List<T> newList){
        dataProvider.getList().clear();
        dataProvider.getList().addAll(newList);
        setVisibleRange(0, newList.size());
        setRowCount(newList.size());
    }

    public void setHovered(DiagramInteractor hovered) {
        this.hoveredItem = null;
        if (hovered != null){
            for (T object : dataProvider.getList()) {
                if (hovered instanceof InteractorEntity && hovered.getAccession().equals(object.getAcc())) {
                    this.hoveredItem = object;
                    break;
                } else if (hovered instanceof InteractorLink && ((InteractorLink) hovered).getId().equals(object.getId())) {
                    this.hoveredItem = object;
                    break;
                }
            }
        }
        applyThreshold();
        highlightHoveredItem();
    }

    public void setHovered(String acc){
        if (acc == null || acc.isEmpty()){
            hoveredItem = null;
        } else {
            for (T object : dataProvider.getList()) {
                if (acc.equals(object.getAcc())) {
                    this.hoveredItem = object;
                    break;
                }
            }
        }
        applyThreshold();
        highlightHoveredItem();
    }

    public void setThreshold(double threshold){
        this.threshold = threshold;
        applyThreshold();
    }

    private void highlightHoveredItem(){
        List<T> list = dataProvider.getList();
        for(int i=0;i<list.size();i++){
            T object = dataProvider.getList().get(i);
            if(object.equals(hoveredItem)){
                getRowElement(i).getStyle().setBackgroundColor(HIGHLIGHT_COLOUR);
                getRowElement(i).getCells().getItem(0).getStyle().setColor("#000000");
                getRowElement(i).getCells().getItem(1).getStyle().setColor("#000000");
                getRowElement(i).getCells().getItem(2).getStyle().setColor("#000000");
                getRowElement(i).scrollIntoView();
            }else{
                getRowElement(i).getStyle().clearBackgroundColor();
            }
        }
    }

    private void applyThreshold(){
        List<T> list = dataProvider.getList();
        for(int i=0;i<list.size();i++){
            T object = dataProvider.getList().get(i);
            if(object.getScore()<threshold ){
                getRowElement(i).getCells().getItem(0).getStyle().setColor("#AAAAAA");
                getRowElement(i).getCells().getItem(1).getStyle().setColor("#AAAAAA");
                getRowElement(i).getCells().getItem(2).getStyle().setColor("#AAAAAA");
            }else{
                getRowElement(i).getCells().getItem(0).getStyle().setColor("#FFFFFF");
                getRowElement(i).getCells().getItem(1).getStyle().setColor("#FFFFFF");
                getRowElement(i).getCells().getItem(2).getStyle().setColor("#FFFFFF");
            }
        }
    }

    private Column<T, String> buildColumnTitle(final boolean showIds) {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                if(showIds){
                    return object.getAcc();
                }else{
                    return object.getAlias()!=null ? object.getAlias() : object.getAcc();
                }
            }
        };
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.ACCESSION));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildEvidencesColumn() {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                Integer evidenceSize = object.getEvidences();
                return ( evidenceSize == null || evidenceSize.equals(0) ) ? "N/A" : "" + evidenceSize;
            }
        };
        columnTitle.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.ID));
            }
        });
        return columnTitle;
    }

    private Column<T, String> buildScoreColumn() {
        Column<T, String> columnTitle = new Column<T, String>(new ClickableTextCell()) {
            @Override
            public String getValue(T object) {
                return NumberFormat.getFormat("0.000").format(object.getScore());
            }
        };
        // This is for setting the column text alignment BUT
        // to do the same for the header you have to go to the CSS
        columnTitle.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));
        columnTitle.setFieldUpdater(new FieldUpdater<T, String>() {
            public void update(int index, T object, String value) {
                fireEvent(new TableItemSelectedEvent(object, TableItemSelectedEvent.Selection.SCORE));
            }
        });
        return columnTitle;
    }

    @Override
    public void redraw() {
        super.redraw();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                applyThreshold();
            }
        });
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

        @ImportedWithPrefix("diagram-InteractorsTable")
        interface MoleculesStyle extends Style {
            String CSS = "org/reactome/web/diagram/context/dialogs/InteractorsTable.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(MoleculesStyle.CSS)
        MoleculesStyle dataGridStyle();
    }
}
