package org.reactome.web.diagram.search.autocomplete.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.search.autocomplete.AutoCompletePanel;
import org.reactome.web.diagram.search.autocomplete.AutoCompleteResult;

/**
 * A custom {@link Cell} used to render the autocomplete items
 *
 */
public class AutoCompleteCell extends AbstractCell<AutoCompleteResult> {
    private final static String TOOLTIP = "Click this to search for ";

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
                    "<div title=\"{1}\" style=\"float:left; margin-left: 5px\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:200px\">" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small; margin-top: 1px; line-height: 1em\">" +
                            "{2}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml minCell(SafeHtml image, String imgTooltip, SafeHtml primary);
    }

    private static Templates templates = GWT.create(Templates.class);

    @Override
    public void render(Context context, AutoCompleteResult value, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (value == null) {
            return;
        }

        Image img = new Image(AutoCompletePanel.RESOURCES.searchSuggestion());
        img.setStyleName(AutoCompletePanel.RESOURCES.getCSS().icon());
        SafeHtml image = SafeHtmlUtils.fromTrustedString(img.toString());

        String imgTooltip = TOOLTIP + value.getResult();
        SafeHtml primary = SafeHtmlUtils.fromTrustedString(value.getDisplayResult());
        sb.append(templates.minCell(image, imgTooltip, primary));
    }
}
