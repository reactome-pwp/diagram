package org.reactome.web.diagram.context.sections;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Section extends Composite implements ScrollHandler {

    private FlexTable headerTable;
    private FlexTable dataTable;

    private ScrollPanel headerScrollPanel;
    private ScrollPanel dataScrollPanel;

    public Section(String title, Integer height){
        FlowPanel sectionHeader = new FlowPanel();
        sectionHeader.setStyleName(RESOURCES.getCSS().sectionHeader());
        Label sectionTitle = new Label(title);
        sectionHeader.add(sectionTitle);

        headerTable = new FlexTable();
        headerTable.setStyleName(RESOURCES.getCSS().headerTable());
        headerTable.setCellPadding(1);
        headerTable.setCellSpacing(1);
        headerScrollPanel = new ScrollPanel();
        headerScrollPanel.setStyleName(RESOURCES.getCSS().scrollPanel());
        headerScrollPanel.add(headerTable);
        headerScrollPanel.addScrollHandler(this);

        dataTable = new FlexTable();
        dataTable.setStyleName(RESOURCES.getCSS().dataTable());
        dataTable.setCellPadding(1);
        dataTable.setCellSpacing(1);
        dataScrollPanel = new ScrollPanel();
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
    public void onScroll(ScrollEvent event) {
        ScrollPanel scrollPanel = (ScrollPanel) event.getSource();
        if(scrollPanel.equals(dataScrollPanel)) {
            headerScrollPanel.setHorizontalScrollPosition(dataScrollPanel.getHorizontalScrollPosition());
        }else if(scrollPanel.equals(headerScrollPanel)){
            dataScrollPanel.setHorizontalScrollPosition(headerScrollPanel.getHorizontalScrollPosition());
        }

    }

    public void setTableHeader(List<String> headerTitles){
        for(int c=0; c<headerTitles.size(); c++) {
            headerTable.setText(0, c, headerTitles.get(c));
            headerTable.getFlexCellFormatter().setHorizontalAlignment(0, c, HasHorizontalAlignment.ALIGN_CENTER);

        }
    }

    public void setTableContents(List<String[]> tableRows){
        for(int r=0; r<tableRows.size(); r++){
            String[] row = tableRows.get(r);
            for(int c=0; c<row.length; c++){
                dataTable.setText(r, c, row[c]);
                dataTable.getFlexCellFormatter().setHorizontalAlignment(r, c, HasHorizontalAlignment.ALIGN_CENTER);
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

        String scrollPanel();

        String dataTable();

    }
}
