package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.images.DatabaseObjectImages;
import org.reactome.web.diagram.data.graph.raw.SubpathwayRaw;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Subpathway extends DatabaseObject {

    private Set<ReactionLikeEvent> containedEvents = new HashSet<>();

    public Subpathway(SubpathwayRaw subpathway) {
        super(subpathway);
    }

    public void addContainedEvent(ReactionLikeEvent rle){
        this.containedEvents.add(rle);
    }

    @Override
    public List<DiagramObject> getDiagramObjects() {
        List<DiagramObject> rtn = new LinkedList<>();
        for (ReactionLikeEvent event : containedEvents) {
            rtn.addAll(event.getDiagramObjects());
        }
        return rtn;
    }

    @Override
    public ImageResource getImageResource() {
        return DatabaseObjectImages.INSTANCE.pathway();
    }

    public Set<ReactionLikeEvent> getContainedEvents() {
        return containedEvents;
    }
}
