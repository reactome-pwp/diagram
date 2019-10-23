package org.reactome.web.diagram.events;

import java.util.Collection;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.handlers.RenderOtherDataHandler;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;

import com.google.gwt.event.shared.GwtEvent;

public class RenderOtherDataEvent extends GwtEvent<RenderOtherDataHandler>{
    public static Type<RenderOtherDataHandler> TYPE = new Type<RenderOtherDataHandler>();

    private RendererManager rendererManager;
    private Collection<DiagramObject> items;
    private AdvancedContext2d ctx;
    private OverlayContext overlay;
    
    public RenderOtherDataEvent(RendererManager rendererManager, Collection<DiagramObject> items, AdvancedContext2d ctx, OverlayContext overlay) {
    	this.rendererManager = rendererManager;
    	this.items = items;
    	this.ctx = ctx;
    	this.overlay = overlay;
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

	public AdvancedContext2d getCtx() {
		return ctx;
	}
	
	public OverlayContext getOverlay() {
		return overlay;
	}
	
	@Override
	public String toString() {
		return "Render Other Data Event Fired!";
	}

}
