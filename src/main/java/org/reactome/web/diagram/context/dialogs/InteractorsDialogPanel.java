package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
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
        Section s = new Section("Interactors");
        ArrayList contents = new ArrayList();
        contents.add(new String[] {"Alpha", "Beta", "Charlie" });
        contents.add(new String[] {"1", "2", "3" });

        s.setTableContents(contents);
        initWidget(s);

    }
}
