package org.reactome.web.diagram.client.visualisers.diagram;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.util.actions.MouseActionsHandlers;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
class UserActionsManager implements MouseActionsHandlers {

    interface Handler {
        HoveredItem getHoveredDiagramObject();
        DiagramInteractor getHoveredInteractor();
        void dragInteractor(InteractorEntity interactor, Coordinate delta);
        void loadDiagram(String stId);
        void mouseZoom(double delta);
        void padding(Coordinate delta);
        void setMousePosition(Coordinate mouse);
        void setSelection(boolean zoom, boolean fireExternally);
        void showDialog(DiagramObject item);
    }

    static final double ZOOM_FACTOR = 0.025;
    static final double ZOOM_DELTA = 0.25;
    static final double ZOOM_TOUCH_DELTA = 200;

    InteractorEntity hoveredInteractor;

    Coordinate mouseDown = null;

    private final DiagramCanvas canvas;
    private Handler handler;

    private boolean diagramMoved = false;
    private boolean interactorDragged = false;
    private Double fingerDistance;

    public UserActionsManager(Handler handler, DiagramCanvas canvas) {
        this.handler = handler;
        this.canvas = canvas;
    }

    @Override
    public void onClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!diagramMoved && !interactorDragged) {
            diagramMoved = false;
            interactorDragged = false;
            handler.setSelection(false, true);
        }
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        HoveredItem hovered = handler.getHoveredDiagramObject();
        DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
        GraphObject toOpen = item != null ? item.getGraphObject() : null;
        if (toOpen instanceof GraphPathway) {
            handler.loadDiagram(toOpen.getDbId().toString());
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        diagramMoved = false;
        interactorDragged = false;
        int button = event.getNativeEvent().getButton();
        switch (button) {
            case NativeEvent.BUTTON_RIGHT:
                HoveredItem hovered = handler.getHoveredDiagramObject();
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                handler.setSelection(false, true);
                handler.showDialog(item);
                break;
            default:
                setMouseDownPosition(event.getRelativeElement(), event);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        setMousePosition(event.getRelativeElement(), event);
        if (mouseDown != null) {
            canvas.setCursor(Style.Cursor.MOVE);
            Element element = event.getRelativeElement();
            Coordinate mouse = CoordinateFactory.get(event.getRelativeX(element), event.getRelativeY(element));
            Coordinate delta = mouse.minus(mouseDown);
            if(hoveredInteractor == null) {
                diagramMoved = true;
                handler.padding(delta);

            } else {
                interactorDragged = true;
                handler.dragInteractor(hoveredInteractor, delta);
            }
            setMouseDownPosition(event.getRelativeElement(), event);
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        diagramMoved = false;
        mouseDown = null;
        canvas.setCursor(Style.Cursor.DEFAULT);
        // With this we force to re-calculate the hovered element
        // (assuming there is nothing in the provided position)
        handler.setMousePosition(CoordinateFactory.get(-2000, -2000));
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation();
        event.preventDefault();
        HoveredItem hovered = handler.getHoveredDiagramObject();
        canvas.setCursor(hovered == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
        mouseDown = null;
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.stopPropagation();
        event.preventDefault();
        setMousePosition(event.getRelativeElement(), event);
        handler.mouseZoom(event.getDeltaY() * ZOOM_FACTOR);
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        mouseDown = null;
        diagramMoved = false;
        interactorDragged = false;
        fingerDistance = null;
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        if (this.diagramMoved || !interactorDragged) {
            //Do NOT use this.mouseCurrent in the next line
            handler.setSelection(false, true);
        }
        mouseDown = null;
        diagramMoved = false;
        interactorDragged = false;
        fingerDistance = null;
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        //TODO: Take into account interactor dragged scenario
        event.stopPropagation();
        event.preventDefault();
        if (mouseDown != null) {
            this.diagramMoved = true;
            //Do NOT use this.mouseCurrent in the next line
            Coordinate mouseCurrent = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
            Coordinate delta = mouseCurrent.minus(this.mouseDown);
            handler.padding(delta);
            this.mouseDown = mouseCurrent;
        } else {
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate delta = finger1.minus(finger2);
            double distance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
            double deltaFactor = (distance - fingerDistance) / ZOOM_TOUCH_DELTA;
            this.fingerDistance = distance;
            handler.mouseZoom(deltaFactor);
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        event.stopPropagation();
        event.preventDefault();
        if (event.getChangedTouches().length() == 1) {
            this.mouseDown = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
        } else {
            this.mouseDown = null;
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate delta = finger1.minus(finger2);
            fingerDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
        }
    }

    public boolean setInteractorHovered(DiagramInteractor hovered){
        if (mouseDown != null) return false;

        if (hovered != null && hovered instanceof InteractorEntity) {
            hoveredInteractor = (InteractorEntity) hovered;
        } else {
            hoveredInteractor = null;
        }
        return true;
    }

    protected void setMouseDownPosition(Element element, MouseEvent event) {
        this.mouseDown = CoordinateFactory.get(event.getRelativeX(element), event.getRelativeY(element));
    }

    protected void setMousePosition(Element element, MouseEvent event) {
        Coordinate mouseCurrent = CoordinateFactory.get(event.getRelativeX(element), event.getRelativeY(element));
        handler.setMousePosition(mouseCurrent);
    }

    protected Coordinate getTouchCoordinate(Touch touch) {
        int x = touch.getRelativeX(canvas.getElement());
        int y = touch.getRelativeY(canvas.getElement());
        return CoordinateFactory.get(x, y);
    }
}
