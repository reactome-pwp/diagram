package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AboutTabPanel extends Composite {

    public AboutTabPanel() {
        FlowPanel fp = new FlowPanel();
        fp.add(new Label("This is the about tab"));
        initWidget(fp);
    }
}
