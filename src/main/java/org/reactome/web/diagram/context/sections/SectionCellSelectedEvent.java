package org.reactome.web.diagram.context.sections;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SectionCellSelectedEvent extends GwtEvent<SectionCellSelectedHandler> {
    public static GwtEvent.Type<SectionCellSelectedHandler> TYPE = new GwtEvent.Type<SectionCellSelectedHandler>();

    private String value;

    public SectionCellSelectedEvent(String value) {
        this.value = value.trim();
    }

    @Override
    public GwtEvent.Type<SectionCellSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SectionCellSelectedHandler handler) {
        handler.onCellSelected(this);
    }

    public String getValue() {
        return value;
    }
}
