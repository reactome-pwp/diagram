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

/**
 * A custom {@link Cell} used to render the recent search items
 *
 */
public class RecentSearchCell extends AbstractCell<String> {

    private final static String TOOLTIP = "Click this to search for ";

    private static SafeHtml icon;
    private static SafeHtml deleteIcon;

    interface Templates extends SafeHtmlTemplates {

        @Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
                    "<div title=\"{1}\" style=\"float:left; margin-left: 5px\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:200px\">" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small; margin-top: 1px; line-height: 1em; width:180px; float:left;\">" +
                            "{2}" +
                        "</div>" +
                        "<a><div class=\"deleteIcon\" title=\"Remove this item\">{3}</div></a>" +
                    "</div>" +
                "</div>")
        SafeHtml minCell(SafeHtml image, String imgTooltip, SafeHtml primary, SafeHtml deleteImage);
    }

    public RecentSearchCell() {
        super();

        Image img = new Image(AutoCompletePanel.RESOURCES.searchRecent());
        img.setStyleName(AutoCompletePanel.RESOURCES.getCSS().icon());
        RecentSearchCell.icon = SafeHtmlUtils.fromTrustedString(img.toString());

        Image delImg = new Image(AutoCompletePanel.RESOURCES.deleteItem());
        delImg.setStyleName(AutoCompletePanel.RESOURCES.getCSS().deleteItem());
        RecentSearchCell.deleteIcon = SafeHtmlUtils.fromTrustedString(delImg.toString());

    }

    private static Templates templates = GWT.create(Templates.class);

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (value == null) {
            return;
        }

        String imgTooltip = TOOLTIP + value;
        SafeHtml primary = SafeHtmlUtils.fromTrustedString(value);

        sb.append(templates.minCell(RecentSearchCell.icon, imgTooltip, primary, RecentSearchCell.deleteIcon));
    }
}

