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
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.events.IllustrationSelectedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ControlActionHandler;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.classes.DatabaseObject;
import org.reactome.web.pwp.model.classes.Figure;
import org.reactome.web.pwp.model.classes.Pathway;
import org.reactome.web.pwp.model.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.handlers.DatabaseObjectCreatedHandler;
import org.reactome.web.pwp.model.handlers.DatabaseObjectLoadedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramIllustrations extends AbstractMenuDialog implements ControlActionHandler,
        ContentLoadedHandler, GraphObjectSelectedHandler {

    private EventBus eventBus;

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
        setIllustrations(event.getContext().getContent().getDbId(), main);
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        GraphObject object = event.getGraphObject();
        Long dbId = null;
        if (object instanceof GraphPathway) {
            GraphPathway pathway = (GraphPathway) object;
            dbId = pathway.getDbId();
        } else if (object instanceof GraphSubpathway) {
            GraphSubpathway subpathway = (GraphSubpathway) object;
            dbId = subpathway.getDbId();
        }
        setIllustrations(dbId, other);
    }

    private void setIllustrations(Long dbId, final FlowPanel panel) {
        panel.clear();
        if (dbId == null) return;
        Label loadingLbl = new Label("Loading...");
        loadingLbl.setStyleName(RESOURCES.getCSS().loading());
        panel.add(loadingLbl);
        DatabaseObjectFactory.get(dbId, new DatabaseObjectCreatedHandler() {
            @Override
            public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
                if (databaseObject instanceof Pathway) {
                    panel.clear();
                    final Pathway pathway = (Pathway) databaseObject;
                    if (pathway.getFigure().isEmpty()) {
                        panel.add(getIllustration(pathway, null));
                    } else {
                        for (Figure figure : pathway.getFigure()) {
                            figure.load(new DatabaseObjectLoadedHandler() {
                                @Override
                                public void onDatabaseObjectLoaded(DatabaseObject databaseObject) {
                                    Figure figure = (Figure) databaseObject;
                                    panel.add(getIllustration(pathway, DiagramFactory.ILLUSTRATION_SERVER + figure.getUrl()));
                                }

                                @Override
                                public void onDatabaseObjectError(Throwable throwable) {
                                    Console.error(throwable.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onDatabaseObjectError(Throwable exception) {
                panel.clear();
                panel.add(getErrorMsg("There was an error retrieving data for the loaded diagram..."));
            }
        });
    }

    private Widget getIllustration(Pathway pathway, final String url) {
        FlowPanel fp = new FlowPanel();
        fp.setStyleName(RESOURCES.getCSS().illustration());
        if (url != null && !url.isEmpty()) {
            Image image = new Image(RESOURCES.illustration());
            fp.add(image);
            Label label = new Label(pathway.getDisplayName());
            label.setText(pathway.getDisplayName());
            fp.add(label);
            Anchor anchor = new Anchor(SafeHtmlUtils.fromTrustedString(fp.toString()), url);
            anchor.addClickHandler(event -> {
                if (!event.isMetaKeyDown() && !event.isControlKeyDown()) event.preventDefault();
                event.stopPropagation();
                hide();
                eventBus.fireEventFromSource(new IllustrationSelectedEvent(url), DiagramIllustrations.this);
            });
            return anchor;
        } else {
            Image image = new Image(RESOURCES.illustrationDisabled());
            fp.add(image);
            Label label = new Label(pathway.getDisplayName());
            label.setText("No illustrations for " + pathway.getDisplayName());
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
