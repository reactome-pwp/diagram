package org.reactome.web.diagram.messages;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;
import org.reactome.web.diagram.events.DiagramLoadedEvent;
import org.reactome.web.diagram.events.DiagramRequestedEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.DiagramInternalErrorHandler;
import org.reactome.web.diagram.handlers.DiagramLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LoadingMessage extends MessagesPanel implements DiagramRequestedHandler, LayoutLoadedHandler, DiagramLoadedHandler, DiagramInternalErrorHandler {

    public LoadingMessage(EventBus eventBus) {
        super(eventBus);

        MessagesPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.loadingMessage());

        FlowPanel fp = new FlowPanel();
        fp.add(new Image(RESOURCES.loader()));
        fp.add(new InlineLabel("Loading diagram..."));
        this.add(fp);

        this.initHandlers();
    }

    @Override
    public void onDiagramInternalError(DiagramInternalErrorEvent event) {
        if(event.getSource().getClass().equals(LoaderManager.class)) {
            this.setVisible(false);
        }
    }

    @Override
    public void onDiagramRequested(DiagramRequestedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onDiagramLoaded(DiagramLoadedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setVisible(false);
    }

    private void initHandlers(){
        this.eventBus.addHandler(DiagramInternalErrorEvent.TYPE, this);
        this.eventBus.addHandler(DiagramRequestedEvent.TYPE, this);
        this.eventBus.addHandler(DiagramLoadedEvent.TYPE, this);
        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }
}
