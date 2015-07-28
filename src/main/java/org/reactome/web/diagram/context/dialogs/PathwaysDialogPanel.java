package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PathwaysDialogPanel extends Composite {

    public PathwaysDialogPanel() {
        initWidget(new InlineLabel("Pathways dialog content"));
    }
}
