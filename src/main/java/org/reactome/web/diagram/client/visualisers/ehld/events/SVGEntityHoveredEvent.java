package org.reactome.web.diagram.client.visualisers.ehld.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.client.visualisers.ehld.handlers.SVGEntityHoveredHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@Deprecated
public class SVGEntityHoveredEvent extends GwtEvent<SVGEntityHoveredHandler> {
    public static Type<SVGEntityHoveredHandler> TYPE = new Type<>();

    private String hovered;

    public SVGEntityHoveredEvent(String hovered) {
        this.hovered = hovered;
    }

    @Override
    public Type<SVGEntityHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SVGEntityHoveredHandler handler) {
        handler.onSVGEntityHovered(this);
    }

    public String getHovered() {
        return hovered;
    }

    @Override
    public String toString() {
        return "SVGEntityHoveredEvent{" +
                "hovered='" + hovered + '\'' +
                '}';
    }
}

