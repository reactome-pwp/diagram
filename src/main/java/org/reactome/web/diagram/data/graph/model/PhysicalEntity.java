package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class PhysicalEntity extends DatabaseObject {

    protected String identifier;
    protected String sampleIdentifier;
    protected List<PhysicalEntity> children = new ArrayList<PhysicalEntity>();
            
    private List<ReactionLikeEvent> isInputIn = new ArrayList<ReactionLikeEvent>();
    private List<ReactionLikeEvent> isOutputIn = new ArrayList<ReactionLikeEvent>();
    private List<ReactionLikeEvent> isCatalystIn = new ArrayList<ReactionLikeEvent>();
    private List<ReactionLikeEvent> isActivatorIn = new ArrayList<ReactionLikeEvent>();
    private List<ReactionLikeEvent> isInhibitorIn = new ArrayList<ReactionLikeEvent>();

    public PhysicalEntity(EntityNode node) {
        super(node);
        this.identifier = node.getIdentifier();
    }

    public boolean addParent(List<PhysicalEntity> parents){
        return this.parents.addAll(parents);
    }

    public boolean addChildren(List<PhysicalEntity> children){
        return this.children.addAll(children);
    }

    public boolean addInputIn(ReactionLikeEvent rle){
        return isInputIn.add(rle);
    }

    public boolean addOutputIn(ReactionLikeEvent rle){
        return isOutputIn.add(rle);
    }

    public boolean addCatalystIn(ReactionLikeEvent rle){
        return isCatalystIn.add(rle);
    }

    public boolean addActivatorIn(ReactionLikeEvent rle){
        return isActivatorIn.add(rle);
    }

    public boolean addInhibitorIn(ReactionLikeEvent rle){
        return isInhibitorIn.add(rle);
    }


    public boolean isHit() {
        return sampleIdentifier!=null;
    }

    public void setIsHit(String sampleIdentifier, List<Double> expression) {
        this.sampleIdentifier = sampleIdentifier;
        this.expression = expression;
    }

    public void resetHit(){
        this.sampleIdentifier = null;
        this.expression = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<String> getParticipants(){
        Set<String> rtn = new HashSet<String>();
        rtn.add(identifier);
        return rtn;
    }

    public Map<String, Double> getParticipantsExpression(int column){
        Map<String, Double> rtn = new HashMap<String, Double>();
        if(this.isHit()) {
            rtn.put(sampleIdentifier, this.getExpression(column));
        }
        return rtn;
    }

    public Set<String> getHitParticipants(){
        Set<String> rtn = new HashSet<String>();
        if(this.isHit()) {
            rtn.add(sampleIdentifier);
        }
        return rtn;
    }

    public Set<PhysicalEntity> getParentLocations() {
        Set<PhysicalEntity> rtn = new HashSet<PhysicalEntity>();
        for (PhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    private Set<PhysicalEntity> getParentDiagramIds(){
        Set<PhysicalEntity> rtn = new HashSet<PhysicalEntity>();
        if(!getDiagramObjects().isEmpty()){
            rtn.add(this);
        }
        for (PhysicalEntity parent : parents) {
            rtn.addAll(parent.getParentDiagramIds());
        }
        return rtn;
    }

    public Set<ReactionLikeEvent> participatesIn(){
        Set<ReactionLikeEvent> rtn = new HashSet<ReactionLikeEvent>();
        for (ReactionLikeEvent rle : isInputIn) {
            rtn.add(rle);
        }
        for (ReactionLikeEvent rle : isOutputIn) {
            rtn.add(rle);
        }
        for (ReactionLikeEvent rle : isCatalystIn) {
            rtn.add(rle);
        }
        for (ReactionLikeEvent rle : isActivatorIn) {
            rtn.add(rle);
        }
        for (ReactionLikeEvent rle : isInhibitorIn) {
            rtn .add(rle);
        }
        return rtn;
    }
}
