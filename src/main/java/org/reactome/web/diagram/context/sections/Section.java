package org.reactome.web.diagram.context.sections;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class Section extends Composite {

    // Holds the data rows of the table
    // This is a list of RowData Object
    private List tableRows = new ArrayList();

    // Holds the data for the column headers
    private List tableHeader = new ArrayList();


    private FlexTable table;

    public Section(String title){
        FlowPanel sectionHeader = new FlowPanel();
        sectionHeader.setStyleName(RESOURCES.getCSS().sectionHeader());
        Label sectionTitle = new Label(title);
        sectionHeader.add(sectionTitle);

        table = new FlexTable();
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(table);

        FlowPanel vp = new FlowPanel();
        vp.add(sectionHeader);
        vp.add(scrollPanel);

        initWidget(vp);
    }

    public void setTableContents(List<String[]> tableRows){
        for(int r=0; r<tableRows.size(); r++){
            String[] row = tableRows.get(r);
            for(int c=0; c<row.length; c++){
                table.setText(r, c, row[c]);
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
    }
}
