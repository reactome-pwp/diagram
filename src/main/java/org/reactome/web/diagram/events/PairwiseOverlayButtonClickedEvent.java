package org.reactome.web.diagram.events;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PairwiseOverlayButtonClickedEvent extends GwtEvent<PairwiseOverlayButtonClickedHandler>{
    public static Type<PairwiseOverlayButtonClickedHandler> TYPE = new Type<>();

    private GraphObject graphObject;
    private String uniprot;
    private String geneName;
    private String type;
    
    /**
     * use to pass whole graph object for a complex
     * @param graphObject
     */
    public PairwiseOverlayButtonClickedEvent(GraphObject graphObject) {
    	this.graphObject = graphObject;
    }
    
    /**
     * used to pass geneName for a single protein
     * type should be the initial type of popup to open. e.g.("TR" or "DG") from SummaryItemAbstractRenderer
     * @param geneName
     */
    public PairwiseOverlayButtonClickedEvent(String uniprot, String geneName, String type) {
    	this.geneName = geneName;
    	this.uniprot = uniprot;
    	this.type = type;
    }
    
    public GraphObject getGraphObject() {
    	return this.graphObject;
    }
    
    public String getGeneName() {
    	return this.geneName;
    }
    
    public String getUniprot() {
    	return this.uniprot;
    }
    
    public String getType() {
    	return this.type;
    }
    
	@Override
	public Type<PairwiseOverlayButtonClickedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseOverlayButtonClickedHandler handler) {
		handler.onPairwiseOverlayButtonClicked(this);
	}

	@Override
	public String toString() {
		if(graphObject != null)
			return "Opening pairwise view for: " + graphObject.getStId();
		else if(geneName != null)
			return "Opening pairwise view for: " + getGeneName();
		else
			return "Opening pairwise view";
	}
	
}
