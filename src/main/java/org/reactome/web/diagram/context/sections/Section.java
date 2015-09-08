package org.reactome.web.diagram.context.sections;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Section extends Composite implements ClickHandler, ScrollHandler {
    private EventBus eventBus;

    private Label sectionTitle;

    private FlexTable headerTable;
    private FlexTable dataTable;

    private ScrollPanel headerScrollPanel;
    private ScrollPanel dataScrollPanel;

    public Section(EventBus eventBus, String title, Integer height){
        this.eventBus = eventBus;

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

    private void hightlightRow(FlexTable table, int row){
        for(int r=0; r<table.getRowCount(); r++){
            if(r==row){
                table.getRowFormatter().addStyleName(r, RESOURCES.getCSS().hightlightedRow());
            }else{
                table.getRowFormatter().removeStyleName(r, RESOURCES.getCSS().hightlightedRow());
            }
        }
    }

    private void hightlightCol(FlexTable table, int col){
        for(int r=0; r<table.getRowCount(); r++) {
            for(int c=0; c<table.getCellCount(r); c++){
                if(c==col){
                    table.getFlexCellFormatter().addStyleName(r, c, RESOURCES.getCSS().hightlightedCol());
                }else{
                    table.getFlexCellFormatter().removeStyleName(r, c, RESOURCES.getCSS().hightlightedCol());
                }
            }
        }
    }


    @Override
    public void onClick(ClickEvent event) {
        FlexTable table = (FlexTable) event.getSource();
        //gets the index of the cell and row the user clicked on
        int cellIndex = table.getCellForEvent(event).getCellIndex();
        int rowIndex = table.getCellForEvent(event).getRowIndex();
        if(table.equals(dataTable)){
//            System.out.println("r:" + rowIndex + " c:" + cellIndex + " -> " + table.getFlexCellFormatter().getElement(rowIndex, cellIndex).getInnerHTML());
            hightlightRow(dataTable, rowIndex);
        }else if(table.equals(headerTable)){
//            System.out.println("r:" + rowIndex + " c:" + cellIndex + " -> " + table.getFlexCellFormatter().getElement(rowIndex, cellIndex).getInnerHTML());
            hightlightCol(headerTable, cellIndex);
            hightlightCol(dataTable, cellIndex+1);
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
            headerTable.setText(0, c, headerTitles.get(c));
            headerTable.getFlexCellFormatter().setHorizontalAlignment(0, c, HasHorizontalAlignment.ALIGN_CENTER);

        }
    }

    public void setTableContents(List<List<String>> tableRows){
        dataTable.removeAllRows();
        for(int r=0; r<tableRows.size(); r++){
            List<String> row = tableRows.get(r);
            for(int c=0; c<row.size(); c++){
                dataTable.setText(r, c, row.get(c));
                dataTable.getFlexCellFormatter().setHorizontalAlignment(r, c, HasHorizontalAlignment.ALIGN_CENTER);
            }
        }
    }

    public void setHeight(int height){
        dataScrollPanel.setHeight(height + "px");
    }

    public void setTitle(String title){
        this.sectionTitle.setText(title);

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

        String dataScrollPanel();
    }
}
