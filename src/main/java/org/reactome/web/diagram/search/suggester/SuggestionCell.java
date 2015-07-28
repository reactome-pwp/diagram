package org.reactome.web.diagram.search.suggester;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.graph.model.GraphObject;

/**
 * A custom {@link Cell} used to render the suggestion for a {@link GraphObject}
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionCell extends AbstractCell<GraphObject> {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">&nbsp;&nbsp;{0}&nbsp;&nbsp;<span>{1}</span></div>")
        SafeHtml cell(SafeHtml image, SafeHtml value);
    }

    private static Templates templates = GWT.create(Templates.class);


    @Override
    public void render(Context context, GraphObject value, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (value == null) {
            return;
        }

//        Next two lines DO NOT work for Chrome
//        final ImagePrototypeElement imageElement = AbstractImagePrototype.create(value.getImageResource()).createElement();
//        final SafeHtml safeImage = new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(imageElement.getString());

        Image image = new Image(value.getImageResource());
        SafeHtml safeImage = SafeHtmlUtils.fromTrustedString(image.toString());

        SafeHtml safeValue = SafeHtmlUtils.fromTrustedString(value.getSearchDisplay());

        SafeHtml rendered = templates.cell(safeImage, safeValue);
        sb.append(rendered);
    }
}
