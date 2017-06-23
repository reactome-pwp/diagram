package org.reactome.web.diagram.util.actions;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface MouseActionsHandlers extends ClickHandler, DoubleClickHandler,
        MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, MouseWheelHandler,
        TouchCancelHandler, TouchEndHandler, TouchMoveHandler, TouchStartHandler,
        Window.ScrollHandler{
}
