package org.reactome.web.diagram.controls.top.illustrations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.controls.navigation.ControlAction;
import org.reactome.web.diagram.controls.top.common.AbstractMenuDialog;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.ControlActionHandler;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Event;
import org.reactome.web.pwp.model.client.classes.Figure;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

import static org.reactome.web.diagram.data.content.Content.Type.SVG;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramIllustrations extends AbstractMenuDialog implements ControlActionHandler,
        ContentLoadedHandler, ContentRequestedHandler, GraphObjectSelectedHandler {

    private EventBus eventBus;
    private Context context;

    private FlowPanel main = new FlowPanel();
    private FlowPanel other = new FlowPanel();

    public DiagramIllustrations(EventBus eventBus) {
        super("Illustrations");
        addStyleName(RESOURCES.getCSS().illustrations());

        this.eventBus = eventBus;
        this.initHandlers();

        initialise();
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        if (event.getAction().equals(ControlAction.FIREWORKS)) {
            hide();
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        initialise();
        context = event.getContext();
        if(!context.getContent().getType().equals(SVG)) {
            setIllustrations(context.getContent().getDbId(), main);
        } else {
            main.add(getErrorMsg("No illustrations found"));
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        if(!context.getContent().getType().equals(SVG)) {
            GraphObject object = event.getGraphObject();
            if (object instanceof GraphEvent) {
                GraphEvent gevent = (GraphEvent) object;
                setIllustrations(gevent.getDbId(), other);
            }
        }
    }

    private void setIllustrations(Long dbId, final FlowPanel panel) {
        panel.clear();
        if (dbId == null) return;
        Label loadingLbl = new Label("Loading...");
        loadingLbl.setStyleName(RESOURCES.getCSS().loading());
        panel.add(loadingLbl);
        ContentClient.query(dbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                if (databaseObject instanceof Event) {
                    panel.clear();
                    final Event event = (Event) databaseObject;
                    if (event.getFigure().isEmpty()) {
                        panel.add(getIllustration(event, null));
                    } else {
                        for (Figure figure : event.getFigure()) {
                            figure.load(new ObjectLoaded() {
                                @Override
                                public void onObjectLoaded(DatabaseObject databaseObject) {
                                    Figure figure = (Figure) databaseObject;
                                    panel.add(getIllustration(event, DiagramFactory.ILLUSTRATION_SERVER + figure.getUrl()));
                                }

                                @Override
                                public void onContentClientException(Type type, String message) {
                                    Console.error(message);
                                }

                                @Override
                                public void onContentClientError(ContentClientError error) {
                                    Console.error(error.getReason());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onContentClientException(Type type, String message) {
                panel.clear();
                panel.add(getErrorMsg("There was an error retrieving data for the loaded diagram..."));
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                panel.clear();
                panel.add(getErrorMsg("There was an error retrieving data for the loaded diagram..."));
            }
        });
    }

    private Widget getIllustration(Event event, final String url) {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName(RESOURCES.getCSS().illustration());
        if (url != null && !url.isEmpty()) {
            Image image = new Image(RESOURCES.illustration());
            fp.add(image);
            Label label = new Label(event.getDisplayName());
            label.setText(event.getDisplayName());
            fp.add(label);
            Anchor anchor = new Anchor(SafeHtmlUtils.fromTrustedString(fp.toString()), url);
            anchor.addClickHandler(e -> {
                if (!e.isMetaKeyDown() && !e.isControlKeyDown()) e.preventDefault();
                e.stopPropagation();
                hide();
                eventBus.fireEventFromSource(new IllustrationSelectedEvent(url), DiagramIllustrations.this);
            });
            return anchor;
        } else {
            Image image = new Image(RESOURCES.illustrationDisabled());
            fp.add(image);
            Label label = new Label(event.getDisplayName());
            label.setText("No illustrations for " + event.getDisplayName());
            label.setStyleName(RESOURCES.getCSS().error());
            fp.add(label);
            return fp;
        }
    }

    private Widget getErrorMsg(String msg) {
        Label label = new Label(msg);
        label.setTitle(msg);
        label.setStyleName(RESOURCES.getCSS().error());
        return label;
    }

    private void initialise() {
        clear();
        main.clear();
        add(main);
        other.clear();
        add(other);
    }

    private void initHandlers() {
        this.eventBus.addHandler(ControlActionEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/illustration.png")
        ImageResource illustration();

        @Source("images/illustration_disabled.png")
        ImageResource illustrationDisabled();
    }

    @CssResource.ImportedWithPrefix("diagram-DiagramIllustrations")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/top/illustrations/DiagramIllustrations.css";

        String illustrations();

        String illustration();

        String error();

        String loading();
    }
}
