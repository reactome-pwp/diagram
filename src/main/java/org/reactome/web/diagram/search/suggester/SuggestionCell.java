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
import org.reactome.web.diagram.search.SearchResultObject;

/**
 * A custom {@link Cell} used to render the suggestion for a {@link GraphObject}
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionCell extends AbstractCell<SearchResultObject> {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
                    "<div title=\"{1}\" style=\"float:left; margin-left: 5px\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:260px\">" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small\">" +
                            "{2}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml minCell(SafeHtml image, String imgTooltip, SafeHtml primary);

        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
                    "<div title=\"{1}\" style=\"float:left;margin: 7px 0 0 5px\">{0}</div>" +
                    "<div style=\"float:left;margin-left:10px; width:260px\">" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small\">" +
                            "{2}" +
                        "</div>" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small;\">" +
                            "{3}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml cell(SafeHtml image, String imgTooltip, SafeHtml primary, SafeHtml secondary);
    }

    private static Templates templates = GWT.create(Templates.class);


    @Override
    public void render(Context context, SearchResultObject value, SafeHtmlBuilder sb) {
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

        Image img = new Image(value.getImageResource());
        SafeHtml image = SafeHtmlUtils.fromTrustedString(img.toString());

        String imgTooltip = value.getSchemaClass().name;
        SafeHtml primary = SafeHtmlUtils.fromTrustedString(value.getPrimarySearchDisplay());
        if(value.getSecondarySearchDisplay().isEmpty()) {
            sb.append(templates.minCell(image, imgTooltip, primary));
        }else{
            SafeHtml secondary = SafeHtmlUtils.fromTrustedString(value.getSecondarySearchDisplay());
            sb.append(templates.cell(image, imgTooltip, primary, secondary));
        }
    }
}
