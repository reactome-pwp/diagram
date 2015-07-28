package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.analysis.*;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;
import org.reactome.web.diagram.data.graph.model.Pathway;
import org.reactome.web.diagram.data.graph.model.PhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.model.Box;
import uk.ac.ebi.pwp.structures.quadtree.model.QuadTree2D;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramContext {
    static final double ANALYSIS_MIN_PERCENTAGE = 0.03;
    //The maximum number of elements for every QuadTree quadrant node
    static final int NUMBER_OF_ELEMENTS = 25;

    private DiagramContent content;
    private DiagramStatus diagramStatus;
    private QuadTree2D<DiagramObject> quadTree;
    private AnalysisStatus analysisStatus;

    public DiagramContext(DiagramContent content) {
        this.content = content;

        this.quadTree = new QuadTree2D<DiagramObject>(
                content.minX,
                content.minY,
                content.maxX,
                content.maxY,
                NUMBER_OF_ELEMENTS
        );

        for (DiagramObject node : content.getDiagramObjects()) {
            this.quadTree.add(node);
        }

        //Status needs to be created every time we load a new content
        this.diagramStatus = new DiagramStatus();
    }

    public void clearAnalysisOverlay(){
        this.analysisStatus = null;
        for (DatabaseObject databaseObject : this.content.getDatabaseObjects()) {
            if(databaseObject instanceof PhysicalEntity) {
                ((PhysicalEntity) databaseObject).resetHit();
            }else if(databaseObject instanceof Pathway){
                ((Pathway) databaseObject).resetHit();
            }
        }
    }

    public void setAnalysisOverlay(AnalysisStatus analysisStatus,PathwayIdentifiers pathwayIdentifiers, List<PathwaySummary> pathwaySummaries){
        this.analysisStatus = analysisStatus;
        MapSet<String, DatabaseObject> map = this.content.getIdentifierMap();
        if(pathwayIdentifiers!=null) {
            for (PathwayIdentifier identifier : pathwayIdentifiers.getIdentifiers()) {
                for (IdentifierMap identifierMap : identifier.getMapsTo()) {
                    for (String id : identifierMap.getIds()) {
                        Set<DatabaseObject> elements = map.getElements(id);
                        if (elements == null) continue;
                        for (DatabaseObject databaseObject : elements) {
                            if(databaseObject instanceof PhysicalEntity) {
                                PhysicalEntity pe = (PhysicalEntity) databaseObject;
                                pe.setIsHit(identifier.getIdentifier(), identifier.getExp());
                            }
                        }
                    }
                }
            }
        }
        if(pathwaySummaries!=null) {
            for (PathwaySummary pathwaySummary : pathwaySummaries) {
                EntityStatistics statistics = pathwaySummary.getEntities();
                if (statistics.getFound() > 0) {
                    //In this case process nodes DO NOT HAVE an identifier, but DO NOT use null here! use empty string
                    Pathway pathway = (Pathway) this.content.getDatabaseObject(pathwaySummary.getDbId());
                    Double percentage = statistics.getFound() / statistics.getTotal().doubleValue();
                    if(percentage<ANALYSIS_MIN_PERCENTAGE) percentage = ANALYSIS_MIN_PERCENTAGE;
                    pathway.setIsHit(percentage, pathwaySummary.getEntities().getExp());
                }
            }
        }
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public Collection<DiagramObject> getHoveredTarget(Coordinate p){
        double f = 1 / this.diagramStatus.getFactor();
        return quadTree.getItems(new Box(p.getX()-f, p.getY()-f, p.getX()+f, p.getY()+f));
    }

    public DiagramContent getContent() {
        return content;
    }

    public DiagramStatus getDiagramStatus() {
        return diagramStatus;
    }

    public Collection<DiagramObject> getVisibleElements(int width, int height){
        Box visibleArea = this.diagramStatus.getVisibleModelArea(width, height);
        return this.quadTree.getItems(visibleArea);
    }

    public Box getVisibleModelArea(int width, int height){
        return this.diagramStatus.getVisibleModelArea(width, height);
    }

    public ColourProfileType getColourProfileType(){
        if(content.getForNormalDraw()==null || content.getForNormalDraw()){
            return ColourProfileType.NORMAL;
        } else {
            return ColourProfileType.FADE_OUT;
        }
    }

    @Override
    public String toString() {
        return "DiagramContext{" +
                "content=" + content +
                ", status=" + diagramStatus +
                '}';
    }
}
