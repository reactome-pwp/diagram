package org.reactome.web.diagram.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.*;

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
        AboutFireworks about = new AboutFireworks();
        about.center();
        about.show();
        if(this.handler!=null){
            handler.onAboutMenuItemSelected();
        }
    }

    class AboutFireworks extends DialogBox {

        public AboutFireworks() {
            super(true, false);
            getCaption().asWidget().getElement().getStyle().setCursor(Style.Cursor.MOVE);
            getCaption().setHTML("About Pathway Diagrams");
            FlowPanel fp = new FlowPanel();
            fp.getElement().getStyle().setFloat(Style.Float.RIGHT);
            fp.add(new HTMLPanel(about.getText()));
            fp.add(new Button("Close", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    hide();
                }
            }));
            add(fp);
        }
    }
}
