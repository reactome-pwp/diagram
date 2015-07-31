package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDialogPanel extends Composite {

    public InteractorsDialogPanel(DiagramObject diagramObject) {
        initWidget(new InlineLabel("Interactors dialog content"));
    }
}
