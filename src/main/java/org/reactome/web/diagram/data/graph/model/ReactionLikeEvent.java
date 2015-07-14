package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EventNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ReactionLikeEvent extends Event {

    private List<PhysicalEntity> inputs = new ArrayList<PhysicalEntity>();
    private List<PhysicalEntity> outputs = new ArrayList<PhysicalEntity>();
    private List<PhysicalEntity> catalysts = new ArrayList<PhysicalEntity>();
    private List<PhysicalEntity> activators = new ArrayList<PhysicalEntity>();
    private List<PhysicalEntity> inhibitors = new ArrayList<PhysicalEntity>();
    private List<PhysicalEntity> requirements = new ArrayList<PhysicalEntity>();

    private List<ReactionLikeEvent> followingEvents = new ArrayList<ReactionLikeEvent>();
    private List<ReactionLikeEvent> precedingEvents = new ArrayList<ReactionLikeEvent>();

    private Compartment compartment;

    public ReactionLikeEvent(EventNode node) {
        super(node);
    }

    public List<PhysicalEntity> getInputs() {
        return inputs;
    }

    public void setInputs(List<PhysicalEntity> inputs) {
        this.inputs = inputs;
        for (PhysicalEntity input : inputs) {
            input.addInputIn(this);
        }
    }

    public List<PhysicalEntity> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<PhysicalEntity> outputs) {
        this.outputs = outputs;
        for (PhysicalEntity output : outputs) {
            output.addOutputIn(this);
        }
    }

    public List<PhysicalEntity> getCatalysts() {
        return catalysts;
    }

    public void setCatalysts(List<PhysicalEntity> catalysts) {
        this.catalysts = catalysts;
        for (PhysicalEntity catalyst : catalysts) {
            catalyst.addCatalystIn(this);
        }
    }

    public List<PhysicalEntity> getActivators() {
        return activators;
    }

    public void setActivators(List<PhysicalEntity> activators) {
        this.activators = activators;
        for (PhysicalEntity activator : activators) {
            activator.addActivatorIn(this);
        }
    }

    public List<PhysicalEntity> getInhibitors() {
        return inhibitors;
    }

    public void setInhibitors(List<PhysicalEntity> inhibitors) {
        this.inhibitors = inhibitors;
        for (PhysicalEntity inhibitor : inhibitors) {
            inhibitor.addInhibitorIn(this);
        }
    }

    public List<ReactionLikeEvent> getFollowingEvents() {
        return followingEvents;
    }

    public void setFollowingEvents(List<ReactionLikeEvent> followingEvents) {
        this.followingEvents = followingEvents;
    }

    public List<ReactionLikeEvent> getPrecedingEvents() {
        return precedingEvents;
    }

    public void setPrecedingEvents(List<ReactionLikeEvent> precedingEvents) {
        this.precedingEvents = precedingEvents;
    }

    public List<PhysicalEntity> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<PhysicalEntity> requirements) {
        this.requirements = requirements;
    }

    public Compartment getCompartment() {
        return compartment;
    }

}
