package org.reactome.web.diagram.context.sections;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Section extends Composite implements ClickHandler, ScrollHandler {
    private Label sectionTitle;

    private FlexTable headerTable;
    private FlexTable dataTable;

    private ScrollPanel headerScrollPanel;
    private ScrollPanel dataScrollPanel;

    public Section(String title, Integer height){
        FlowPanel sectionHeader = new FlowPanel();
        sectionHeader.setStyleName(RESOURCES.getCSS().sectionHeader());
        sectionTitle = new Label(title);
        sectionHeader.add(sectionTitle);

        headerTable = new FlexTable();
        headerTable.setStyleName(RESOURCES.getCSS().headerTable());
        headerTable.setCellPadding(1);
        headerTable.setCellSpacing(1);
        headerTable.addClickHandler(this);
        headerScrollPanel = new ScrollPanel();
        headerScrollPanel.setStyleName(RESOURCES.getCSS().headerScrollPanel());
        headerScrollPanel.add(headerTable);
        headerScrollPanel.addScrollHandler(this);

        dataTable = new FlexTable();
        dataTable.setStyleName(RESOURCES.getCSS().dataTable());
        dataTable.setCellPadding(1);
        dataTable.setCellSpacing(1);
        dataTable.addClickHandler(this);
        dataScrollPanel = new ScrollPanel();
        dataScrollPanel.setStyleName(RESOURCES.getCSS().dataScrollPanel());
        dataScrollPanel.setHeight(height + "px");
        dataScrollPanel.add(dataTable);
        dataScrollPanel.addScrollHandler(this);

        FlowPanel vp = new FlowPanel();
        vp.add(sectionHeader);
        vp.add(headerScrollPanel);
        vp.add(dataScrollPanel);

        initWidget(vp);
    }

    @Override
    public void onClick(ClickEvent event) {
        FlexTable table = (FlexTable) event.getSource();
        //gets the index of the cell and row the user clicked on
//        int cellIndex = table.getCellForEvent(event).getCellIndex();
        int rowIndex = table.getCellForEvent(event).getRowIndex();
        if(table.equals(dataTable)){
            hightlightRow(dataTable, rowIndex, RESOURCES.getCSS().hightlightedRow());
        }
    }

    @Override
    public void onScroll(ScrollEvent event) {
        ScrollPanel scrollPanel = (ScrollPanel) event.getSource();
        if(scrollPanel.equals(dataScrollPanel)) {
            headerScrollPanel.setHorizontalScrollPosition(dataScrollPanel.getHorizontalScrollPosition());
        }else if(scrollPanel.equals(headerScrollPanel)){
            dataScrollPanel.setHorizontalScrollPosition(headerScrollPanel.getHorizontalScrollPosition());
        }
    }

    public void setTableHeader(List<String> headerTitles){
        headerTable.removeAllRows();
        for(int c=0; c<headerTitles.size(); c++) {
            headerTable.setWidget(0, c, new Label(headerTitles.get(c)));
            headerTable.getFlexCellFormatter().setHorizontalAlignment(0, c, HasHorizontalAlignment.ALIGN_CENTER);

        }
    }

    public void setTableContents(List<List<String>> tableRows){
        dataTable.removeAllRows();
        FlexTable.FlexCellFormatter flexCellFormatter = dataTable.getFlexCellFormatter();
        for(int r=0; r<tableRows.size(); r++){
            List<String> row = tableRows.get(r);
            for(int c=0; c<row.size(); c++){
                dataTable.setWidget(r, c, new Label(row.get(c)));
                flexCellFormatter.setHorizontalAlignment(r, c, HasHorizontalAlignment.ALIGN_CENTER);
                if(row.size()==1){
                    flexCellFormatter.addStyleName(r, c, RESOURCES.getCSS().largeCell());
                }else{
                    flexCellFormatter.removeStyleName(r, c, RESOURCES.getCSS().largeCell());
                }
            }
        }
    }

    public void applyAnalysisColours(List<List<String>> tableRows, Double min, Double max){
        for(int r=0; r<tableRows.size(); r++){
            List<String> row = tableRows.get(r);
            for(int c=1; c<row.size(); c++){
                String cell = row.get(c);
                if(!cell.isEmpty() ){
                    Double value = Double.parseDouble(row.get(c));
                    String colour = AnalysisColours.get().expressionGradient.getColor(value, min, max);
                    dataTable.getCellFormatter().getElement(r,c).getStyle().setBackgroundColor(colour);
                    dataTable.getCellFormatter().getElement(r,c).getStyle().setColor("#000000");
                }
            }
        }
    }

    public void setHeight(int height){
        dataScrollPanel.setHeight(height + "px");
    }

    public void setTitle(String title){
        this.sectionTitle.setText(title);

    }

    public void selectExpressionCol(int col){
        if(headerTable!=null && dataTable!=null) {
            ensureVisible(headerScrollPanel, headerTable, 0, col);
            hightlightCol(headerTable, col, RESOURCES.getCSS().hightlightedCol());
            hightlightCol(dataTable, col + 1, RESOURCES.getCSS().selectedExpressionColumn());
        }
    }

    private void hightlightRow(FlexTable table, int row, String style){
        if(table==null) return;
        for(int r=0; r<table.getRowCount(); r++){
            if(r==row){
                table.getRowFormatter().addStyleName(r, style);
            }else{
                table.getRowFormatter().removeStyleName(r, style);
            }
        }
    }

    private void hightlightCol(FlexTable table, int col, String style){
        if(table==null) return;
        for(int r=0; r<table.getRowCount(); r++) {
            for(int c=0; c<table.getCellCount(r); c++){
                if(c==col){
                    table.getFlexCellFormatter().addStyleName(r, c, style);
                }else{
                    table.getFlexCellFormatter().removeStyleName(r, c, style);
                }
            }
        }
    }

    private void ensureVisible(ScrollPanel scrollPanel, FlexTable table, int row, int col){
        if(scrollPanel!=null && table!=null) {
            Element element = table.getWidget(row, col).getElement();
            if(element!=null) {
                element.scrollIntoView();
            }else{
                System.out.println("Widget null!");
            }
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

    @CssResource.ImportedWithPrefix("diagram-sections")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/sections/section.css";

        String sectionHeader();

        String headerTable();

        String headerScrollPanel();

        String dataTable();

        String hightlightedRow();

        String hightlightedCol();

        String selectedExpressionColumn();

        String largeCell();

        String dataScrollPanel();
    }
}
