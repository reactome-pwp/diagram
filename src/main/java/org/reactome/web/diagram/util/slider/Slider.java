package org.reactome.web.diagram.util.slider;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;

import java.text.ParseException;

/**
 * A basic implementation for a progress slider based on canvas
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Slider extends Composite implements HasHandlers, MouseMoveHandler, MouseDownHandler, MouseOutHandler, MouseUpHandler, ValueChangeHandler {
    private Canvas canvas;
    private DoubleBox valueTb;
    private SliderBar bar;
    private SliderPin pin;
    private PinStatus pinStatus = PinStatus.STD;
    private double percentage = 0.0;

    public Slider(int width, int height, double initialPercentage, boolean includeTextBox) {
        this.canvas = Canvas.createIfSupported();
        if(this.canvas !=null){
            this.canvas.setWidth(width + "px");
            this.canvas.setHeight(height + "px");
            this.canvas.setCoordinateSpaceWidth(width);
            this.canvas.setCoordinateSpaceHeight(height);

            if(includeTextBox) {
                this.valueTb = new DoubleBox();
                Style style = this.valueTb.getElement().getStyle();
                style.setWidth(23, Style.Unit.PX);
                style.setHeight(17, Style.Unit.PX);
                style.setMarginTop(3, Style.Unit.PX);
                style.setFloat(Style.Float.RIGHT);
                style.setBackgroundColor("#6E6E6E");
                style.setColor("#FFFFFF");
                style.setBorderWidth(0, Style.Unit.PX);
            }

            FlowPanel fp = new FlowPanel();
            fp.add(this.canvas);
            if(valueTb!=null) {
                fp.add(this.valueTb);
            }
            this.initWidget(fp);
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
            if(valueTb!=null) {
                valueTb.setText("" + percentage);
            }
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
        if(valueTb!=null) {
            this.valueTb.addValueChangeHandler(this);
        }
    }

    private void initialise(double width, double height, double percentage){
        this.percentage = percentage;
        if(valueTb!=null) {
            valueTb.setText("" + percentage);
        }
        initHandlers();

        double tick = height / 7.0;
        double y = tick * 3;

        int cR = (int) Math.round(tick * 2);
        int cX = (int) Math.round((width - (2 * cR)) * percentage) + cR;
        int cY = (int) Math.round(height / 2.0);

        this.pin = new SliderPin(cX, cY, cR);
        this.bar = new SliderBar(tick, y, cR);

        this.draw();
    }

    private boolean pinHovered(MouseEvent event){
        return this.pin.isPointInside(getMousePosition(event));
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        double percentage = 0;
        try {
            percentage = this.valueTb.getValueOrThrow();
            if(percentage < 0){
                percentage = 0.0;
            }else if(percentage > 1.0){
                percentage = 1.0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int cX = (int) (Math.round((this.canvas.getOffsetWidth() - (2 * this.pin.r)) * percentage) + this.pin.r);
        Point pos = new Point(cX, this.pin.pos.y);
        this.pin.setNewPos(pos);
        this.draw();
        checkPinMoved();
    }
}
