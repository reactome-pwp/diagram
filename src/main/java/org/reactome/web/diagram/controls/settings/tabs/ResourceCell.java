package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * A custom {@link Cell} used to render the suggestion for a {@link ResourceObject}
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResourceCell extends AbstractCell<ResourceObject> {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("" +
                "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis;\">" +
                    "<div style=\"float:left; margin-left: 5px\">{0}</div>" +
                    "<div style=\"float:left; margin-left:10px; width:200px\">" +
                        "<div style=\"overflow:hidden; white-space:nowrap; text-overflow:ellipsis; font-size:small\">" +
                            "{1}" +
                        "</div>" +
                    "</div>" +
                "</div>")
        SafeHtml cell(SafeHtml image, SafeHtml primary);
    }

    private static Templates templates = GWT.create(Templates.class);


    @Override
    public void render(Cell.Context context, ResourceObject value, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (value == null) {
            return;
        }

        Image img = new Image(value.isStatus() ? InteractorsTabPanel.RESOURCES.active() : InteractorsTabPanel.RESOURCES.inActive());
        SafeHtml image = SafeHtmlUtils.fromTrustedString(img.toString());

        SafeHtml name = SafeHtmlUtils.fromTrustedString(value.getName());
        sb.append(templates.cell(image, name));
    }
}
