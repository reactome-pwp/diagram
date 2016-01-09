package org.reactome.web.diagram.util.interactors;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLayout {

    private static final int RADIUS = 100;

    private String acc;
    private Node node;

    public InteractorsLayout(Node node) {
        this.node = node;
        GraphPhysicalEntity pe = node.getGraphObject();
        this.acc = pe.getIdentifier(); //And that my soon is how we get the accession ;)
    }

    public String getAcc() {
        return acc;
    }

    public Node getNode() {
        return node;
    }

    public void doLayout(InteractorEntity entity, int i, int n){
        if (entity == null || entity.isLaidOut() || n == 0) return;

        double delta = 360 / (double) n;
        double angle = delta * i;
        Coordinate center = node.getPosition();
        double x = center.getX() + RADIUS * Math.cos(angle);
        double y = center.getY() + RADIUS * Math.sin(angle);

        entity.setMinX(x - 15);
        entity.setMaxX(x + 15);
        entity.setMinY(y - 10);
        entity.setMaxY(y + 10);
    }
}
