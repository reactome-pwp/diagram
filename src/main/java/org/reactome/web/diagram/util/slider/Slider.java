package org.reactome.web.diagram.util.slider;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A basic implementation for a progress slider based on canvas
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Slider extends Composite implements HasHandlers, MouseMoveHandler, MouseDownHandler, MouseOutHandler, MouseUpHandler,
        ValueBoxUpdatedHandler {
    private Canvas canvas;
    private ValueBox valueTb;
    private SliderBar bar;
    private SliderPin pin;
    private PinStatus pinStatus = PinStatus.STD;
    private double percentage = 0.0;

    public Slider(int width, int height, double initialPercentage, boolean includeTextBox) {
        this.percentage = initialPercentage;
        this.canvas = Canvas.createIfSupported();
        if(this.canvas !=null){
            this.canvas.setWidth(width + "px");
            this.canvas.setHeight(height + "px");
            this.canvas.setCoordinateSpaceWidth(width);
            this.canvas.setCoordinateSpaceHeight(height);
            this.canvas.setStyleName(RESOURCES.getCSS().slider());
            FlowPanel fp = new FlowPanel();
            fp.add(this.canvas);

            if(includeTextBox) {
                this.valueTb = new ValueBox(initialPercentage);
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
            updateValueBox(getPercentageFromPinPosition());
            checkPinMoved();
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

    @Override
    public void onValueBoxUpdated(ValueBoxUpdatedEvent event) {
        setValue(event.getValue());
    }

    public void setValue(double value){
        int cX = (int) (Math.round((this.canvas.getOffsetWidth() - (2 * this.pin.r)) * value) + this.pin.r);
        Point pos = new Point(cX, this.pin.pos.y);
        this.pin.setNewPos(pos);
        this.draw();
        checkPinMoved();
    }

    private void checkPinMoved(){
        double percentage = getPercentageFromPinPosition();
        if(this.percentage!=percentage){
            this.percentage = percentage;
            updateValueBox(percentage);
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
            this.valueTb.addValueBoxUpdatedHandler(this);
        }
    }

    private void initialise(double width, double height, double percentage){
        this.percentage = percentage;
        updateValueBox(percentage);
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

    private double getPercentageFromPinPosition(){
        int x = this.pin.pos.x - (int) this.pin.r;
        double w = this.canvas.getOffsetWidth() - 2 * this.pin.r;
        return Math.round( (x / w) * 100) / 100.0;
    }

    private void updateValueBox(Double percentage){
        if(valueTb!=null) {
            valueTb.setText("" + percentage);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-slider")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/util/Slider.css";

        String slider();

        String sliderValueBox();
    }
}
