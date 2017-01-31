package org.reactome.web.diagram.thumbnail.diagram;

import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ThumbnailRendererManager {

    private static final ThumbnailRendererManager manager = new ThumbnailRendererManager();

    private Map<String, org.reactome.web.diagram.thumbnail.render.ThumbnailRenderer> thumbnailMap = new HashMap<String, org.reactome.web.diagram.thumbnail.render.ThumbnailRenderer>();

    public ThumbnailRendererManager() {
        initialiseRenderers();
    }

    public static ThumbnailRendererManager get() {
        return manager;
    }

    public org.reactome.web.diagram.thumbnail.render.ThumbnailRenderer getRenderer(DiagramObject item){
        if(item==null) return null;
        return thumbnailMap.get(item.getRenderableClass());
    }

    private void initialiseRenderers(){
//        thumbnailMap.put("OrgGkRenderNote", new NoteThumbnailRenderer());
        thumbnailMap.put("Compartment", new org.reactome.web.diagram.thumbnail.render.CompartmentThumbnailRenderer());
        thumbnailMap.put("Protein", new org.reactome.web.diagram.thumbnail.render.ProteinThumbnailRenderer());
        thumbnailMap.put("Chemical", new org.reactome.web.diagram.thumbnail.render.ChemicalThumbnailRenderer());
        thumbnailMap.put("Reaction", new org.reactome.web.diagram.thumbnail.render.ReactionThumbnailRenderer());
        thumbnailMap.put("Complex", new org.reactome.web.diagram.thumbnail.render.ComplexThumbnailRenderer());
        thumbnailMap.put("Entity", new org.reactome.web.diagram.thumbnail.render.OtherEntityThumbnailRenderer());
        thumbnailMap.put("EntitySet", new org.reactome.web.diagram.thumbnail.render.SetThumbnailRenderer());
        thumbnailMap.put("ProcessNode", new org.reactome.web.diagram.thumbnail.render.ProcessNodeThumbnailRenderer());
        thumbnailMap.put("FlowLine", new org.reactome.web.diagram.thumbnail.render.FlowlineThumbnailRenderer());
        thumbnailMap.put("Gene", new org.reactome.web.diagram.thumbnail.render.GeneThumbnailRenderer());
        thumbnailMap.put("RNA", new org.reactome.web.diagram.thumbnail.render.RNAThumbnailRenderer());
//        aux = new LinkThumbnailRenderer();
//        thumbnailMap.put("EntitySetAndMemberLink", aux);
//        thumbnailMap.put("EntitySetAndEntitySetLink", aux);
    }
}
