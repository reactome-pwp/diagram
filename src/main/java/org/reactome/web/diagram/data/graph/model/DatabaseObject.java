package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.model.factory.SchemaClass;
import org.reactome.web.diagram.data.graph.raw.GraphNode;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.LinkedList;
import java.util.List;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class DatabaseObject implements Comparable<DatabaseObject> {
    private Long dbId;
    private String stId;
    private String displayName;
    private String searchDisplay;

    List<PhysicalEntity> parents = new LinkedList<PhysicalEntity>();

    private List<DiagramObject> diagramObjects;


    protected List<Double> expression;

    public DatabaseObject(GraphNode node) {
        this.dbId = node.getDbId();
        this.stId = node.getStId();
        this.displayName = node.getDisplayName();
        this.diagramObjects = new LinkedList<DiagramObject>();
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
        return new LinkedList<DiagramObject>(diagramObjects);
    }

    public List<Double> getExpression() {
        return expression;
    }

    public Double getExpression(int column){
        return expression.get(column);
    }

    public String getSearchDisplay() {
        return searchDisplay;
    }

    public void setSearchDisplay(String[] searchTerms) {
        this.searchDisplay = this.displayName;

        if(searchTerms==null || searchTerms.length==0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length()-1, sb.length()).append(")");
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
        this.searchDisplay = regExp.replace(this.searchDisplay, "<u><strong>$1</strong></u>");
    }

    public void clearSearchDisplayValue(){
        this.searchDisplay = null;
    }

    public SchemaClass getSchemaClass(){
        return SchemaClass.getSchemaClass(getClass().getSimpleName());
    }

    public String getClassName(){
        return getSchemaClass().name;
    }

    public abstract ImageResource getImageResource();

    @Override
    public int compareTo(DatabaseObject o) {
        int cmp = getDisplayName().compareTo(o.getDisplayName());
        if(cmp==0){
            cmp = getDbId().compareTo(o.getDbId());
        }
        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseObject that = (DatabaseObject) o;

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
