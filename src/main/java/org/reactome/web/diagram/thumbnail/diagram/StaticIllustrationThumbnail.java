package org.reactome.web.diagram.thumbnail.diagram;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.client.StaticIllustrationPanel;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Event;
import org.reactome.web.pwp.model.client.classes.Figure;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

public class StaticIllustrationThumbnail extends FlowPanel implements ContentRequestedHandler, ContentLoadedHandler, GraphObjectSelectedHandler {

    private EventBus eventBus;
    private Context context;

    private Long loadeddbId;
    private FlowPanel mainStaticIllustrationFlowPanel;
    private FlowPanel selectStaticIllustrationFlowPanel;

    private StaticIllustrationPanel staticIllustrationPanel;
    private OMSVGSVGElement svg;

    // TODO set them to NULL once a diagarm changed
    private String diagramIllustrationURL = null;
    private boolean diagramFigureLoadingInProgress = false;

    private String selectionIllustrationURL = null;
    private boolean selectionFigureLoadingInProgress = false;
    private Map<Content.Type, Visualiser> visualisers;
    private Visualiser activeVisualiser;

    public StaticIllustrationThumbnail(EventBus eventBus, Map<Content.Type, Visualiser> visualisers, StaticIllustrationPanel staticIllustrationPanel)  {
        this.eventBus = eventBus;
        this.visualisers = visualisers;
        this.staticIllustrationPanel = staticIllustrationPanel;

        initHandlers();
    }

    private void initHandlers() {
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
    }

    public void addDiagramFigureToThumbnails(){
        this.loadeddbId = this.context.getContent().getDbId();

        resetAllStaticIllustration();

        if (activeVisualiser instanceof SVGVisualiser) return;

        diagramFigureLoadingInProgress = true;
        ContentClient.query(loadeddbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                diagramFigureLoadingInProgress = false;
                if (databaseObject instanceof Event) {
                    final Event event = (Event) databaseObject;
                    for (Figure figure : event.getFigure()) {
                        diagramIllustrationURL = figure.getUrl();
                        if (selectionIllustrationURL == null && !selectionFigureLoadingInProgress) {
                            createMainStaticIllustrationFlowPanel(databaseObject, diagramIllustrationURL);
                        }
                    }
                }
            }

            @Override
            public void onContentClientException(ContentClientHandler.Type type, String message) {
                diagramFigureLoadingInProgress = false;
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                diagramFigureLoadingInProgress = false;
            }
        });

    }

    public void addSelectionFigureToThumbnails(GraphObject selection) {
        Long dbId = selection.getDbId();

        resetStaticIllustrationSelection();

       // if (activeVisualiser instanceof SVGVisualiser) return;

        selectionFigureLoadingInProgress = true;
        ContentClient.query(dbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                selectionFigureLoadingInProgress = false;
                if (databaseObject instanceof Event) {
                    final Event event = (Event) databaseObject;
                    for (Figure figure : event.getFigure()) {
                        selectionIllustrationURL = figure.getUrl();
                        if (diagramIllustrationURL == null && !diagramFigureLoadingInProgress) {
                            createSelectStaticIllustration(databaseObject, selectionIllustrationURL);
                        }
                    }
                }
            }

            @Override
            public void onContentClientException(ContentClientHandler.Type type, String message) {
                selectionFigureLoadingInProgress = false;
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                selectionFigureLoadingInProgress = false;
            }
        });
    }

    public void createMainStaticIllustrationFlowPanel(final DatabaseObject databaseObject, final String url) {
        mainStaticIllustrationFlowPanel = new FlowPanel();
        mainStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());
        mainStaticIllustrationFlowPanel.getElement().getStyle().setLeft(getThumbnailCurrentPosition() + 10, Style.Unit.PX);

        if (url != null && !url.isEmpty()) {
            Image image = new Image(url);
            image.setUrl(url);
            image.setTitle("Illustration for " + databaseObject.getDisplayName());
            image.setAltText("Illustration for " + databaseObject.getDisplayName());
            image.addClickHandler(clickEvent -> {
                staticIllustrationPanel.setStaticIllustrationUrl(url);
                staticIllustrationPanel.toggle();
//                if (staticIllustrationPanel.getStyleName().contains(StaticIllustrationPanel.RESOURCES.getCSS().panelShown())) {
//                    staticIllustrationPanel.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelHidden());
//                } else {
//                    staticIllustrationPanel.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelShown());
//                }
            });
            mainStaticIllustrationFlowPanel.add(image);
            mainStaticIllustrationFlowPanel.setVisible(true);
        }
        add(mainStaticIllustrationFlowPanel);
    }

    public void createSelectStaticIllustration(final DatabaseObject databaseObject, final String url) {
        selectStaticIllustrationFlowPanel = new FlowPanel();

        selectStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());
        selectStaticIllustrationFlowPanel.addStyleName(RESOURCES.getCSS().selectedStaticThumbnails());
        selectStaticIllustrationFlowPanel.getElement().getStyle().setLeft(getThumbnailCurrentPosition() + 10, Style.Unit.PX);

        if (url != null && !url.isEmpty()) {
            Image image = new Image(url);
            image.setUrl(url);
            image.setTitle("Illustration for " + databaseObject.getDisplayName());
            image.setAltText("Illustration for " + databaseObject.getDisplayName());
            image.addClickHandler(clickEvent -> {
                staticIllustrationPanel.setStaticIllustrationUrl(url);
                staticIllustrationPanel.toggle();
//                if (staticIllustrationPanel.getStyleName().contains(StaticIllustrationPanel.RESOURCES.getCSS().panelShown())) {
//                    staticIllustrationPanel.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelHidden());
//                } else {
//                    staticIllustrationPanel.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelShown());
//                }
            });
            selectStaticIllustrationFlowPanel.add(image);
            selectStaticIllustrationFlowPanel.setVisible(true);
        }
        add(selectStaticIllustrationFlowPanel);
    }

    private int getThumbnailCurrentPosition() {
        if (activeVisualiser == null) {
            return 0;
        }
        if (activeVisualiser instanceof DiagramVisualiser) {
            return ((DiagramVisualiser) activeVisualiser).getDiagramThumbnail().getOffsetWidth();
        }
        if (activeVisualiser instanceof SVGVisualiser) {
            return ((SVGVisualiser) activeVisualiser).getSVGThumbnail().getOffsetWidth();
        }
        return 0;
    }

    public void resetStaticIllustrationSelection() {
        if (staticIllustrationPanel != null) {
            staticIllustrationPanel.reset();
            staticIllustrationPanel.clear();
        }

        diagramIllustrationURL = null;
        diagramFigureLoadingInProgress = false;
        selectionIllustrationURL = null;
        selectionFigureLoadingInProgress = false;

        if (selectStaticIllustrationFlowPanel != null) remove(selectStaticIllustrationFlowPanel);

    }

    public void resetAllStaticIllustration() {
        resetStaticIllustrationSelection();
        if (mainStaticIllustrationFlowPanel != null ) remove(mainStaticIllustrationFlowPanel);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
        activeVisualiser = visualisers.get(context.getContent().getType());
        if (event.getContext().getContent().getType() == DIAGRAM) {
            addDiagramFigureToThumbnails();
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        resetAllStaticIllustration();
        this.context = null;
    }

    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    @Override
    public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
        if (selectStaticIllustrationFlowPanel !=null) remove(selectStaticIllustrationFlowPanel);
        if (event.getGraphObject() != null) {
            addSelectionFigureToThumbnails(event.getGraphObject());
        }
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-StaticIllustrationThumbnail")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/thumbnail/StaticIllustrationThumbnail.css";

        String mainStaticThumbnails();

        String selectedStaticThumbnails();

    }
}
