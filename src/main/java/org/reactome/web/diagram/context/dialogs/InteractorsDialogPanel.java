package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsDialogPanel extends Composite {

    public InteractorsDialogPanel() {
        initWidget(new InlineLabel("Interactors dialog content"));
    }
}
