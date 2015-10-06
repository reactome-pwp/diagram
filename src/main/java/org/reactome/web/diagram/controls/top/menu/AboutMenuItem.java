package org.reactome.web.diagram.controls.top.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuItem;
import org.reactome.web.diagram.controls.top.menu.submenu.AboutDialog;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AboutMenuItem extends MenuItem implements Scheduler.ScheduledCommand {

    interface AboutMenuItemSelectedHandler {
        void onAboutMenuItemSelected();
    }

    private TextResource about;
    private AboutMenuItemSelectedHandler handler;

    public AboutMenuItem(TextResource about, AboutMenuItemSelectedHandler handler) {
        super(new SafeHtmlBuilder().appendEscaped("About...").toSafeHtml());
        setScheduledCommand(this);
        this.about = about;
        this.handler = handler;
    }

    @Override
    public void execute() {
        AboutDialog aboutDialog = new AboutDialog(about);
        aboutDialog.center();
        aboutDialog.show();
        if(this.handler!=null){
            handler.onAboutMenuItemSelected();
        }
    }
}
