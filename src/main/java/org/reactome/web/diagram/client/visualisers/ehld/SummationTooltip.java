package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.controls.top.common.AbstractMenuDialog;
import org.reactome.web.diagram.util.Console;
import org.vectomatic.dom.svg.OMSVGRect;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SummationTooltip extends PopupPanel implements ClickHandler, MouseWheelHandler  {
    private static SummationTooltip tooltip;
    private static int DELAY_TO_SHOW = 1000;
    private static int DELAY_TO_HIDE = 500;

    private Timer timer;

    private int offsetX;
    private int offsetY;
    private int distance;
    private boolean isRight = false;
    private boolean isBottom = false;

    private Label titleLabel;
    private Label summationLabel;

    private SummationTooltip() {
        this.setStyleName(RESOURCES.getCSS().popup());
        this.addStyleName(RESOURCES.getCSS().popupArrow());
//        setModal(false);
        FlowPanel content = new FlowPanel();

        titleLabel = new Label();
        titleLabel.setStyleName(RESOURCES.getCSS().titleLabel());

        summationLabel = new Label();
        summationLabel.setStyleName(RESOURCES.getCSS().summationLabel());

        IconButton closeBtn = new IconButton("", AbstractMenuDialog.RESOURCES.closeNormal(), event -> hide());
        closeBtn.setStyleName(RESOURCES.getCSS().closeBtn());

        SimplePanel summationPanel = new SimplePanel();
        summationPanel.setStyleName(RESOURCES.getCSS().summationPanel());
        summationPanel.add(summationLabel);

        IconButton openBtn = new IconButton("Open Pathway", AbstractMenuDialog.RESOURCES.closeNormal(), event -> this.removeStyleName(RESOURCES.getCSS().popup()));
        openBtn.setStyleName(RESOURCES.getCSS().openBtn());

        IconButton moreInfoBtn = new IconButton("More Info", AbstractMenuDialog.RESOURCES.closeNormal());
        moreInfoBtn.setStyleName(RESOURCES.getCSS().openBtn());

        content.add(titleLabel);
        content.add(closeBtn);
        content.add(summationPanel);
        content.add(openBtn);
        content.add(moreInfoBtn);
        add(content);

        timer = new Timer( ) {
            @Override
            public void run() {
                showWithDelay();
            }
        };

        addHandler(this, MouseWheelEvent.getType());
    }

    public static SummationTooltip get() {
        if (tooltip == null) {
            tooltip = new SummationTooltip();
        }
        return tooltip;
    }

    @Override
    public void onClick(ClickEvent event) {

    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault(); event.stopPropagation();
    }

    public SummationTooltip setPopupTitle(String text){
        titleLabel.setText(text);
        titleLabel.setTitle(text);
        return this;
    }

    public SummationTooltip setSummation(String text){
        summationLabel.setText(text);
        return this;
    }

    public void hide() {
        setVisible(false);
        if (timer.isRunning()) { timer.cancel(); }
    }

    public SummationTooltip positionAndShow(OMSVGRect target, OMSVGRect svg, float zFactor, Widget viewPort) {
        if (timer.isRunning()) { timer.cancel(); }
        calculatePosition(264, 300, target, svg, zFactor, viewPort);
        timer.schedule(DELAY_TO_SHOW);
        return this;
    }

    private void showWithDelay() {
        setVisible(false);
        show();
        setPosition(this.offsetX + this.distance, this.offsetY + this.distance);
        updateArrow(isRight, isBottom);
        setVisible(true);
    }

    private void setPosition(int left, int top) {
        Element elem = getElement();
        elem.getStyle().setPropertyPx("left", left);
        elem.getStyle().setPropertyPx("top", top);
    }

    private void calculatePosition(int dialogWidth, int dialogHeight, OMSVGRect target, OMSVGRect svg, float zFactor, Widget viewport){
        float left = viewport.getAbsoluteLeft();
        float top = viewport.getAbsoluteTop();
        isBottom = false; isRight = false;

        int viewHeight = viewport.getOffsetHeight();
        int viewWidth = viewport.getOffsetWidth();

        float leftSpace = (target.getX() * zFactor) + svg.getX();
        float rightSpace = viewWidth - ((target.getMaxX() * zFactor) + svg.getX());
        float topSpace = (target.getY() * zFactor) + svg.getY();
        float bottomSpace = viewHeight - ((target.getMaxY() * zFactor) + svg.getY());

//        Console.error("Zoom:" + zFactor);
//        Console.error("Target:" + target.getDescription());
//        Console.error("SVG:" + svg.getDescription());
//        Console.error("LeftSpace:" + leftSpace);
//        Console.error("RightSpace:" + rightSpace);
//        Console.error("TopSpace:" + topSpace);
//        Console.error("BottomSpace:" + bottomSpace);

        // Horizontal positioning
        if (rightSpace > dialogWidth) {
            left += ((target.getX() + target.getWidth()) * zFactor) + svg.getX();                    // Place it right
            isRight = true;
        } else if (leftSpace > dialogWidth) {
            left += (target.getX() * zFactor) + svg.getX() - dialogWidth;                            // Place it left
        }else {
            left += svg.getX();                                                                      // Extreme case
        }

        // Vertical positioning
        if (bottomSpace > dialogHeight) {
            top += ((target.getY() + 3 * target.getHeight()/4) * zFactor) + svg.getY();                  // Place it bottom
            isBottom = true;
        } else {
            top += ((target.getY() + target.getHeight()/2) * zFactor) + svg.getY() - dialogHeight;   // Place it top
        }

        this.offsetX = (int)left;
        this.offsetY = (int)top;
        this.distance = (int)distance;
    }

    private void updateArrow(boolean isRight, boolean isBottom) {
        this.removeStyleName(RESOURCES.getCSS().arrowTopRight());
        this.removeStyleName(RESOURCES.getCSS().arrowBottomRight());
        this.removeStyleName(RESOURCES.getCSS().arrowTopLeft());
        this.removeStyleName(RESOURCES.getCSS().arrowBottomLeft());

        if (isBottom) {
            if (isRight) {
                Console.error("Bottom");
                this.addStyleName(RESOURCES.getCSS().arrowBottomRight());
            } else {
                this.addStyleName(RESOURCES.getCSS().arrowBottomLeft());
            }
        } else {
            if (isRight) {
                this.addStyleName(RESOURCES.getCSS().arrowTopRight());
            } else {
                this.addStyleName(RESOURCES.getCSS().arrowTopLeft());
            }
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

    @CssResource.ImportedWithPrefix("diagram-SummationTooltip")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/tooltips/SummationTooltip.css";

        String popup();

        String titleLabel();

        String closeBtn();

        String summationPanel();

        String summationLabel();

        String openBtn();

        String popupArrow();

        String arrowTopRight();

        String arrowBottomRight();

        String arrowTopLeft();

        String arrowBottomLeft();

    }

}
