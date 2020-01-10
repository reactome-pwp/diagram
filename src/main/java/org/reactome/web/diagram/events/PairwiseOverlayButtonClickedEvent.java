package org.reactome.web.diagram.events;

import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PairwiseOverlayButtonClickedEvent extends GwtEvent<PairwiseOverlayButtonClickedHandler>{
    public static Type<PairwiseOverlayButtonClickedHandler> TYPE = new Type<>();

    private String diagramObjectAccession;
    
    public PairwiseOverlayButtonClickedEvent(String diagramObjectAccession) {
    	this.diagramObjectAccession = diagramObjectAccession;
    }
    
    public String getDiagramObjectAccession() {
    	return this.diagramObjectAccession;
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
		return "Opening pairwise view for: " + diagramObjectAccession;
	}
	
}
