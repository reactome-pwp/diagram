package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.GraphObjectImages;
import org.reactome.web.diagram.data.graph.raw.SubpathwayRaw;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphSubpathway extends GraphObject {

    private Set<GraphReactionLikeEvent> containedEvents = new HashSet<>();

    public GraphSubpathway(SubpathwayRaw subpathway) {
        super(subpathway);
    }

    public void addContainedEvent(GraphReactionLikeEvent rle){
        this.containedEvents.add(rle);
    }

    @Override
    public List<DiagramObject> getDiagramObjects() {
        List<DiagramObject> rtn = new LinkedList<>();
        for (GraphReactionLikeEvent event : containedEvents) {
            rtn.addAll(event.getDiagramObjects());
        }
        return rtn;
    }

    @Override
    public ImageResource getImageResource() {
        return GraphObjectImages.INSTANCE.pathway();
    }

    public Set<GraphReactionLikeEvent> getContainedEvents() {
        return containedEvents;
    }
}
