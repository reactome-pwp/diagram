package org.reactome.web.diagram.tooltips;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@Deprecated
class PathwayInfoPanel extends Composite {

    PathwayInfoPanel(DiagramObject pathway) {
        initWidget(new InlineLabel(pathway.getDisplayName()));
    }
}
