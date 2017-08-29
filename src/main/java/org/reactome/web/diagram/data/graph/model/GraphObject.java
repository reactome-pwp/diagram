package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.raw.GraphNode;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;
import org.reactome.web.diagram.data.layout.Connector;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

import java.util.*;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class GraphObject implements Comparable<GraphObject>, SearchResultObject {
    private Long dbId;
    private String stId;
    private String displayName;
    private String primarySearchDisplay;
    protected String secondarySearchDisplay;

    List<GraphPhysicalEntity> parents = new LinkedList<>();

    private List<DiagramObject> diagramObjects;

    protected List<Double> expression;

    public GraphObject(GraphNode node) {
        this.dbId = node.getDbId();
        this.stId = node.getStId();
        this.displayName = node.getDisplayName();
        this.diagramObjects = new LinkedList<>();
    }

    public GraphObject(SubpathwayNode subpathway) {
        this.dbId = subpathway.getDbId();
        this.stId = subpathway.getStId();
        this.displayName = subpathway.getDisplayName();
        this.diagramObjects = new LinkedList<>();
    }

    public boolean addDiagramObject(DiagramObject diagramObject) {
        return this.diagramObjects.add(diagramObject);
    }

    public Long getDbId() {
        return dbId;
    }

    public String getStId() {
        return stId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<DiagramObject> getDiagramObjects() {
        return new LinkedList<>(diagramObjects);
    }

    public List<Double> getExpression() {
        return expression;
    }

    public Double getExpression(int column) {
        return expression.get(column);
    }

    public String getPrimarySearchDisplay() {
        return primarySearchDisplay;
    }

    public String getSecondarySearchDisplay() {
        return secondarySearchDisplay;
    }

    public void setSearchDisplay(String[] searchTerms) {
        this.primarySearchDisplay = this.displayName;
        this.secondarySearchDisplay = getSecondaryDisplayName();

        if (searchTerms == null || searchTerms.length == 0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length() - 1, sb.length()).append(")");
        String term = sb.toString();
        /**
         * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
         *                    be referred later.
         * gi               : global search and case insensitive
         * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
         *                    that is the reference to the first matched group. This means that we want to
         *                    replace it using the exact word that was found.
         */
        RegExp regExp = RegExp.compile(term, "gi");
        this.primarySearchDisplay = regExp.replace(this.primarySearchDisplay, "<u><strong>$1</strong></u>");
        this.secondarySearchDisplay = regExp.replace(this.secondarySearchDisplay, "<u><strong>$1</strong></u>");
    }

    protected String getSecondaryDisplayName(){
        return stId;
    }

    public void clearSearchDisplayValue() {
        this.primarySearchDisplay = null;
        this.secondarySearchDisplay = null;
    }

    public SchemaClass getSchemaClass() {
        return SchemaClass.getSchemaClass(getClass().getSimpleName().replace("Graph", ""));
    }

    public String getClassName() {
        return getSchemaClass().name;
    }

    public abstract ImageResource getImageResource();

    public Set<DiagramObject> getRelatedDiagramObjects() {
        Set<DiagramObject> toDisplay = new HashSet<>();
        if (this instanceof GraphReactionLikeEvent) {
            toDisplay.addAll(getElementsToDisplay((GraphReactionLikeEvent) this));
        } else if (this instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) this;
            for (GraphReactionLikeEvent rle : pe.participatesIn()) {
                toDisplay.addAll(getElementsToDisplay(rle));
            }
        } else if (this instanceof GraphPathway) {
            toDisplay.addAll(this.getDiagramObjects());
        } else if (this instanceof GraphSubpathway) {
            GraphSubpathway subpathway = (GraphSubpathway) this;
            //DO NOT CALL subpathway.getDiagramObjects here because we need also the participants :)
            for (GraphObject obj : subpathway.getContainedEvents()) {
                if(obj instanceof GraphReactionLikeEvent) {
                    GraphReactionLikeEvent rle = (GraphReactionLikeEvent) obj;
                    toDisplay.addAll(getElementsToDisplay(rle));
                }
            }
        }
        return toDisplay;
    }

    private Collection<DiagramObject> getElementsToDisplay(GraphReactionLikeEvent rle) {
        Set<DiagramObject> toDisplay = new HashSet<>(rle.getDiagramObjects());
        Set<Long> target = new HashSet<>();
        for (DiagramObject diagramObject : toDisplay) {
            target.add(diagramObject.getId());
        }
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getInputs(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getOutputs(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getCatalysts(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getActivators(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getInhibitors(), target));
        toDisplay.addAll(getDiagramObjectsParticipatingInReaction(rle.getRequirements(), target));
        return toDisplay;
    }

    private Collection<DiagramObject> getDiagramObjectsParticipatingInReaction(Collection<GraphPhysicalEntity> entities,
                                                                                      Set<Long> target) {
        Set<DiagramObject> rtn = new HashSet<>();
        for (GraphPhysicalEntity entity : entities) {
            for (DiagramObject object : entity.getDiagramObjects()) {
                if (object instanceof Node) {
                    Node node = (Node) object;
                    for (Connector connector : node.getConnectors()) {
                        if (target.contains(connector.getEdgeId())) {
                            rtn.add(node);
                        }
                    }
                }
            }
        }
        return rtn;
    }


    @Override
    public int compareTo(GraphObject o) {
        int cmp = getDisplayName().compareTo(o.getDisplayName());
        if (cmp == 0) {
            cmp = getDbId().compareTo(o.getDbId());
        }
        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphObject that = (GraphObject) o;

        return !(dbId != null ? !dbId.equals(that.dbId) : that.dbId != null);

    }

    @Override
    public int hashCode() {
        return dbId != null ? dbId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "dbId=" + dbId +
                ", stId='" + stId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", diagramObjects=" + diagramObjects +
                '}';
    }
}
