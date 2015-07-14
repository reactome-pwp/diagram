package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class Event extends DatabaseObject {

    public Event(EventNode node) {
        super(node);
    }

    //Needed when an Event is created from a Encapsulated Pathway in a diagram
    public Event(EntityNode node) {
        super(node);
    }

}
