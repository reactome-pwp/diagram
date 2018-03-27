package org.reactome.web.diagram.search.results.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.search.results.ResultItem;


/**
 * The Cell used to render a {@link ResultItem}.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultItemCell extends AbstractCell<ResultItem> {

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

//        @SafeHtmlTemplates.Template("" +
//                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
//                    "<div title=\"{1}\" style=\"float:left;margin: 7px 0 0 5px\">{0}</div>" +
//                    "<div style=\"float:left;margin-left:10px; width:260px\">" +
//                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small\">" +
//                            "{2}" +
//                        "</div>" +
//                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small;\">" +
//                            "{3}" +
//                        "</div>" +
//                    "</div>" +
//                "</div>")
//        SafeHtml cell(SafeHtml image, String imgTooltip, SafeHtml primary, SafeHtml secondary);

        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; height:45px; border-bottom:#efefef solid 1px \">" +
                    "<div title=\"{1}\" style=\"float:left; margin:15px 0 0 5px;\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:306px;\">" +
                        "<div title=\"{2}\" style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small;\">" +
                            "{2}" +
                        "</div>" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#89c053;\">" +
                            "{3}" +
                        "</div>" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; margin-top:-2px; font-size:x-small; color:#f5b945;\">" +
                            "{4}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml cell(SafeHtml image, String imgTooltip, String primary, SafeHtml secondary, SafeHtml tertiary);
    }

    private static Templates templates = GWT.create(Templates.class);

    @Override
    public void render(Context context, ResultItem value, SafeHtmlBuilder sb) {
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
        String primary = value.getPrimarySearchDisplay();
        SafeHtml secondary = SafeHtmlUtils.fromTrustedString(value.getSecondarySearchDisplay());
        SafeHtml tertiary = SafeHtmlUtils.fromTrustedString(value.getTertiarySearchDisplay());

        sb.append(templates.cell(image, imgTooltip, primary, secondary, tertiary));
    }
}

