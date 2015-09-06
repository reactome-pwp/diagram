package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.context.sections.Section;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.ArrayList;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDialogPanel extends Composite {

    public InteractorsDialogPanel(DiagramObject diagramObject) {
//        initWidget(new InlineLabel("Interactors dialog content"));
        FlowPanel vp = new FlowPanel();
        Section s1 = new Section("Interactors", 70);
        ArrayList header = new ArrayList();
        header.add("Name");
        header.add("1h" );
        header.add("5h" );
        header.add("10h");
        header.add("15h");
        header.add("25h");
        header.add("30h");
        header.add("35h");
        header.add("48h");

        ArrayList contents = new ArrayList();
//        contents.add(new String[] {"Name", "1h", "5h", "10h", "15h", "25h", "30h", "35h", "48h"});
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });
        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.5453" });

        s1.setTableHeader(header);
        s1.setTableContents(contents);

//        Section s2 = new Section("More Interactors", 50);
//        s2.setTableHeader(header);
//        s2.setTableContents(contents);
        vp.add(s1);
//        vp.add(s2);
        initWidget(vp);

    }
}
