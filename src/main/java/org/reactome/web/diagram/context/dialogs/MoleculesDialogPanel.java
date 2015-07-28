package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MoleculesDialogPanel extends Composite {

    public MoleculesDialogPanel() {
        initWidget(new InlineLabel("Molecules dialog content"));
    }
}
