package org.reactome.web.diagram.data.content;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class EHLDObject implements DiagramObject {
    private Long id;
    private Long reactomeId;
    private String stableId;
    private String displayName;
    private String schemaClass;
    private String renderableClass;

    public EHLDObject(Long id, String stableId) {
        this.id = id;
        this.stableId = stableId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getReactomeId() {
        return reactomeId;
    }

    public String getStableId() {return stableId;}

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getSchemaClass() {
        return schemaClass;
    }

    @Override
    public String getRenderableClass() {
        return renderableClass;
    }

    @Override
    public Coordinate getPosition() {
        return null;
    }

    @Override
    public Boolean getIsDisease() {
        return null;
    }

    @Override
    public Boolean getIsFadeOut() {
        return null;
    }

    @Override
    public <T extends GraphObject> T getGraphObject() {
        return null;
    }

    @Override
    public void setGraphObject(GraphObject obj) {

    }

    @Override
    public ContextMenuTrigger contextMenuTrigger() {
        return null;
    }

    @Override
    public double getMinX() {
        return 0;
    }

    @Override
    public double getMinY() {
        return 0;
    }

    @Override
    public double getMaxX() {
        return 0;
    }

    @Override
    public double getMaxY() {
        return 0;
    }
}
