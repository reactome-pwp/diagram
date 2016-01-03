package org.reactome.web.diagram.renderers.impl.s100;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.impl.abs.NoteAbstractRenderer;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class NoteRenderer100 extends NoteAbstractRenderer {
    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }
}
