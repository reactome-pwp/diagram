package org.reactome.web.diagram.controls.top.menu.submenu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuItem;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisProfileMenuBar extends SubMenuBar{

    public interface AnalysisProfileColourChangedHandler {
        void onAnalysisProfileColourChanged(AnalysisProfile profile);
    }

    public AnalysisProfileMenuBar(final AnalysisProfileColourChangedHandler handler) {
        super(true);
        setAnimationEnabled(true);

        String selected = AnalysisColours.get().getSelectedProfileName();
        for (final String name : AnalysisColours.ProfileType.getProfiles()) {
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

                        AnalysisProfile p = AnalysisColours.ProfileType.getByName(name).getAnalysisProfile();
                        handler.onAnalysisProfileColourChanged(p);
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
