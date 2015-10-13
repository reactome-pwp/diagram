package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.view.client.ListDataProvider;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MoleculesTable<T extends GraphPhysicalEntity> extends DataGrid<T> {

    private List<Column<T, String>> expression = new ArrayList<>();

    public MoleculesTable(String name, List<T> molecules, List<String> expression, Double min, Double max, int sel) {
        super(molecules.size(), (MoleculesTableResource) GWT.create(MoleculesTableResource.class));
        setAlwaysShowScrollBars(false);
        Column<T, String> type = buildColumnTitle();
        setColumnWidth(type, 80, Unit.PX);
        addColumn(type, name);
        addExpressionColumns(expression, min, max, sel);

        Collections.sort(molecules, GraphPhysicalEntity.getIdentifierComparator());
        //We have to move all the molecules without expression at the very end
        List<T> list = new LinkedList<>();
        List<T> tailList = new LinkedList<>();
        for (T molecule : molecules) {
            if (molecule.getExpression() == null || molecule.getExpression().isEmpty()) tailList.add(molecule);
            else list.add(molecule);
        }
        list.addAll(tailList);

        ListDataProvider<T> dataProvider = new ListDataProvider<>(list);
        dataProvider.addDataDisplay(this);
    }

    public void addExpressionColumns(List<String> expression, Double min, Double max, int sel) {
        if (expression != null && min!=null && max!=null) {
            for (int i = 0; i < expression.size(); i++) {
                Column<T, String> exp = buildColumnExpression(i, min, max);
                exp.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));
                this.expression.add(exp);
                this.addColumn(exp, expression.get(i));
                this.setColumnWidth(exp, 60, Unit.PX);
            }
        }
        highlightExpColumn(sel);
    }

    public void highlightExpColumn(int col) {
        col++; //This correction is needed since there is a column in front of the expression columns
        for (int i = 1; i <= expression.size(); i++) {
            removeColumnStyleName(i, RESOURCES.getCSS().selectedExpression());
            addColumnStyleName(i, RESOURCES.getCSS().unselectedExpression());
            if (i == col) {
                addColumnStyleName(i, RESOURCES.getCSS().selectedExpression());
            }
        }
        try {
            getRowElement(0).getCells().getItem(col).scrollIntoView();
        } catch (Exception e) {
            //Nothing here
        }
    }

    public void removeExpressionColumns() {
        for (Column<T, String> expColumn : expression) {
            removeColumn(expColumn);
        }
        expression.clear();
    }

    private Column<T, String> buildColumnTitle() {
        Column<T, String> columnTitle = new Column<T, String>(new TextCell()) {
            @Override
            public String getValue(T object) {
                return object.getIdentifier() != null && !object.getIdentifier().isEmpty() ? object.getIdentifier() : object.getStId();
            }
        };
        columnTitle.setSortable(true);
        return columnTitle;
    }

    private Column<T, String> buildColumnExpression(final int col, double min, double max) {
        return new Column<T, String>(new ExpressionCell(min, max)) {
            @Override
            public String getValue(T object) {
                Double exp = (object.getExpression() != null) ? object.getExpression().get(col) : null;
                return exp != null ? NumberFormat.getFormat("#.##E0").format(exp) : "";
            }
        };
    }


    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @ClientBundle.Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @ImportedWithPrefix("diagram-MoleculesTableExpressionSelection")
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
