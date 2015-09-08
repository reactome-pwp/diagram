package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.event.shared.EventBus;
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

    public InteractorsDialogPanel(EventBus eventBus, DiagramObject diagramObject) {
//        initWidget(new InlineLabel("Interactors dialog content"));
        FlowPanel vp = new FlowPanel();
        Section s1 = new Section(eventBus, "Interactors", 35);
        ArrayList header = new ArrayList();
//        header.add("Name");
        header.add("1h" );
        header.add("5h" );
        header.add("10h");
        header.add("15h");
        header.add("25h");
        header.add("30h");
        header.add("35h");
        header.add("48h");


        ArrayList contents = new ArrayList();
        for(int i=0;i<10;i++){
            ArrayList contentLine = new ArrayList();
            contentLine.add("P123456");
            contentLine.add("2.54" );
            contentLine.add("3.01" );
            contentLine.add("5.21" );
            contentLine.add("3.0"  );
            contentLine.add("2.1"  );
            contentLine.add("3.434"); 
            contentLine.add("2.12" );
            contentLine.add("3.54" );

            contents.add(contentLine);
        }


//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.53" });
//        contents.add(new String[] {"P123456", "2.54", "3.01" , "5.21", "3.0", "2.1", "3.434", "2.12", "3.54" });

        s1.setTableHeader(header);
        s1.setTableContents(contents);

        Section s2 = new Section(eventBus, "More Interactors", 35);
        s2.setTableHeader(header);
        s2.setTableContents(contents);
        vp.add(s1);
        vp.add(s2);
        initWidget(vp);

    }
}
