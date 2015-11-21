package org.reactome.web.diagram.controls.top.menu.submenu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuItem;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramProfileMenuBar extends SubMenuBar {

    public interface DiagramProfileColourChangedHandler {
        void onDiagramProfileColourChanged(DiagramProfile profile);
    }

    public DiagramProfileMenuBar(final DiagramProfileColourChangedHandler handler) {
        super(true);
        setAnimationEnabled(true);

        String selected = DiagramColours.get().getSelectedProfileName();
        for (final String name : DiagramColours.ProfileType.getProfiles()) {
            final MenuItem item = new MenuItem(new SafeHtmlBuilder().appendEscaped(name).toSafeHtml());

            if(name.equals(selected)){
                flagItemAsSelected(item);
            }else{
                flagItemAsNormal(item);
            }

            item.setScheduledCommand(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    if (handler != null) {
                        for (MenuItem itemTemp : getItems()) {
                            flagItemAsNormal(itemTemp);
                        }
                        flagItemAsSelected(item);

                        DiagramProfile p = DiagramColours.ProfileType.getByName(name).getDiagramProfile();
                        handler.onDiagramProfileColourChanged(p);
                    }
                }
            });
            addItem(item);
        }
    }

    private void flagItemAsSelected(MenuItem item){
        Style style = item.getElement().getStyle();
        style.setFontWeight(Style.FontWeight.BOLDER);
        style.setTextDecoration(Style.TextDecoration.UNDERLINE);
    }

    private void flagItemAsNormal(MenuItem item){
        Style style = item.getElement().getStyle();
        style.setFontWeight(Style.FontWeight.LIGHTER);
        style.setTextDecoration(Style.TextDecoration.NONE);
    }
}