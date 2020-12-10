package org.reactome.web.diagram.events;

import java.util.Collection;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class RenderOtherDataEvent extends GwtEvent<RenderOtherDataHandler>{
    public static Type<RenderOtherDataHandler> TYPE = new Type<RenderOtherDataHandler>();

    private RendererManager rendererManager;
    private Collection<DiagramObject> items;
    private AdvancedContext2d overlay;
    private OverlayContext overlayContext;
    
    public RenderOtherDataEvent(RendererManager rendererManager, Collection<DiagramObject> items, AdvancedContext2d overlay, OverlayContext overlayContext) {
    	this.rendererManager = rendererManager;
    	this.items = items;
    	this.overlay = overlay;
    	this.overlayContext = overlayContext;
    }
    
	@Override
	public Type<RenderOtherDataHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenderOtherDataHandler handler) {
		handler.onRenderOtherData(this);
	}

	public RendererManager getRendererManager() {
		return rendererManager;
	}

	public Collection<DiagramObject> getItems() {
		return items;
	}

	public AdvancedContext2d getOverlay() {
		return overlay;
	}
	
	public OverlayContext getOverlayContext() {
		return overlayContext;
	}
	
	@Override
	public String toString() {
		return "Render Other Data Event Fired!";
	}

}
