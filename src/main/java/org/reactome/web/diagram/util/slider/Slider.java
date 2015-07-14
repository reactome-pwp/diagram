package org.reactome.web.diagram.util.slider;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;

/**
 * A basic implementation for a progress slider based on canvas
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Slider extends Composite implements HasHandlers, MouseMoveHandler, MouseDownHandler, MouseOutHandler, MouseUpHandler {
    private Canvas canvas;
    private SliderBar bar;
    private SliderPin pin;
    private PinStatus pinStatus = PinStatus.STD;
    private double percentage = 0.0;

    public Slider(int width, int height, double initialPercentage) {
        this.canvas = Canvas.createIfSupported();
        if(this.canvas !=null){
            this.canvas.setWidth(width + "px");
            this.canvas.setHeight(height + "px");
            this.canvas.setCoordinateSpaceWidth(width);
            this.canvas.setCoordinateSpaceHeight(height);

            this.initWidget(this.canvas);
            this.initialise(width, height, initialPercentage);
        }
    }

    public HandlerRegistration addSliderValueChangedHandler(SliderValueChangedHandler handler){
        return addHandler(handler, SliderValueChangedEvent.TYPE);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        this.pinStatus = this.pinHovered(event) ? PinStatus.CLICKED : PinStatus.STD ;
        this.pin.setDownPoint(getMousePosition(event));
        draw();
    }


    @Override
    public void onMouseMove(MouseMoveEvent event) {
        if(this.pinHovered(event)){
            getElement().getStyle().setCursor(Style.Cursor.POINTER);
        }else{
            getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        }
        if(!this.pinStatus.equals(PinStatus.CLICKED)){
            this.pinStatus = pinHovered(event) ? PinStatus.HOVERED : PinStatus.STD;
        }else{
            this.pin.setPos(getMousePosition(event), this.canvas.getOffsetWidth(), (int) this.pin.r);
        }
        draw();
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        checkPinMoved();
        this.pinStatus = PinStatus.STD;
        draw();
    }


    @Override
    public void onMouseUp(MouseUpEvent event) {
        checkPinMoved();
        this.pinStatus = pinHovered(event) ? PinStatus.HOVERED : PinStatus.STD;
        draw();
    }

    private void checkPinMoved(){
        int x = this.pin.pos.x - (int) this.pin.r;
        double w = this.canvas.getOffsetWidth() - 2 * this.pin.r;
        double percentage = Math.round( (x / w) * 100) / 100.0;
        if(this.percentage!=percentage){
            this.percentage = percentage;
            fireEvent(new SliderValueChangedEvent(percentage));
        }
    }

    private void draw(){
        Context2d ctx = this.canvas.getContext2d();
        ctx.clearRect(0, 0, this.canvas.getOffsetWidth(), this.canvas.getOffsetHeight());
        this.bar.draw(ctx);
        this.pin.draw(ctx, this.pinStatus.colour);
    }

    private Point getMousePosition(MouseEvent event){
        int x = event.getRelativeX(this.canvas.getElement());
        int y = event.getRelativeY(this.canvas.getElement());
        return new Point(x,y);
    }

    private void initHandlers(){
        this.canvas.addMouseDownHandler(this);
        this.canvas.addMouseMoveHandler(this);
        this.canvas.addMouseOutHandler(this);
        this.canvas.addMouseUpHandler(this);
    }

    private void initialise(double width, double height, double percentage){
        this.percentage = percentage;
        initHandlers();

        double tick = height / 7.0;
        double y = tick * 3;

        int cR = (int) Math.round(tick * 2);
        int cX = (int) Math.round(width * percentage) + cR;
        int cY = (int) Math.round(height / 2.0);

        this.pin = new SliderPin(cX, cY, cR);
        this.bar = new SliderBar(tick, y, cR);

        this.draw();
    }

    private boolean pinHovered(MouseEvent event){
        return this.pin.isPointInside(getMousePosition(event));
    }
}
