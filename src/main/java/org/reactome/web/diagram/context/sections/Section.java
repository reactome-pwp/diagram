package org.reactome.web.diagram.context.sections;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Section extends Composite {

    private FlexTable headerTable;
    private FlexTable dataTable;

    public Section(String title, Integer height){
        FlowPanel sectionHeader = new FlowPanel();
        sectionHeader.setStyleName(RESOURCES.getCSS().sectionHeader());
        Label sectionTitle = new Label(title);
        sectionHeader.add(sectionTitle);

        headerTable = new FlexTable();
        headerTable.setStyleName(RESOURCES.getCSS().headerTable());
        headerTable.setCellPadding(1);
        headerTable.setCellSpacing(1);
        ScrollPanel headerScrollPanel = new ScrollPanel();
//        headerScrollPanel.setAlwaysShowScrollBars(false);

//        headerScrollPanel.setStyleName(RESOURCES.getCSS().scrollPane());
        headerScrollPanel.add(headerTable);
        SimplePanel sp = new SimplePanel();
        sp.add(headerScrollPanel);
        sp.getElement().getStyle().setOverflowX(Style.Overflow.HIDDEN);

        dataTable = new FlexTable();
        dataTable.setStyleName(RESOURCES.getCSS().dataTable());
        dataTable.setCellPadding(1);
        dataTable.setCellSpacing(1);
        ScrollPanel dataScrollPanel = new ScrollPanel();
        dataScrollPanel.setHeight(height + "px");
        dataScrollPanel.add(dataTable);

        FlowPanel vp = new FlowPanel();
        vp.add(sectionHeader);
        vp.add(sp);
//        vp.add(dataScrollPanel);

        initWidget(vp);
    }

    public void setTableHeader(List<String> headerTitles){
        for(int c=0; c<headerTitles.size(); c++) {
            headerTable.setText(0, c, headerTitles.get(c));

        }
    }

    public void setTableContents(List<String[]> tableRows){
        for(int r=0; r<tableRows.size(); r++){
            String[] row = tableRows.get(r);
            for(int c=0; c<row.length; c++){
                dataTable.setText(r, c, row[c]);
//                if(r==0){
//                    table.getFlexCellFormatter().addStyleName(r,c, RESOURCES.getCSS().sectionTableHeader());
//                }
                dataTable.getColumnFormatter().setWidth(c, "40px");
                if(r>0 && c>0) {
                    dataTable.getFlexCellFormatter().setHorizontalAlignment(r, c, HasHorizontalAlignment.ALIGN_CENTER);
                }
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

        String scrollPane();

        String dataTable();

    }
}
