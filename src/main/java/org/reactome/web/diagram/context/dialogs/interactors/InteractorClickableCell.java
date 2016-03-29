package org.reactome.web.diagram.context.dialogs.interactors;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorClickableCell extends ClickableTextCell {

    @Override
    protected void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
        if(value != null) {
            sb.appendHtmlConstant("<span style='overflow:hidden; white-space:nowrap; text-overflow:ellipsis;' title='" + value.asString() + "'>" + value.asString() + "</span>");
        }
    }

}
