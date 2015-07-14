package org.reactome.web.diagram.search.suggester;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.data.graph.model.DatabaseObject;

/**
 * A custom {@link Cell} used to render the suggestion for a {@link DatabaseObject}
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionCell extends AbstractCell<DatabaseObject> {

    /**
     * The HTML templates used to render the cell.
     */
    interface Templates extends SafeHtmlTemplates {
//        /**
//         * The template for this Cell, which includes styles and a value.
//         *
//         * @param styles the styles to include in the style attribute of the div
//         * @param value  the safe value. Since the value type is {@link SafeHtml},
//         *               it will not be escaped before including it in the template.
//         *               Alternatively, you could make the value type String, in which
//         *               case the value would be escaped.
//         * @return a {@link SafeHtml} instance
//         */
//        @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
//        SafeHtml _cell(SafeStyles styles, SafeHtml value);

        @SafeHtmlTemplates.Template("<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">&nbsp;&nbsp;{0}&nbsp;&nbsp;<span>{1}</span></div>")
        SafeHtml cell(SafeHtml image, SafeHtml value);
    }

    /**
     * Create a singleton instance of the templates used to render the cell.
     */
    private static Templates templates = GWT.create(Templates.class);


    @Override
    public void render(Context context, DatabaseObject value, SafeHtmlBuilder sb) {
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
