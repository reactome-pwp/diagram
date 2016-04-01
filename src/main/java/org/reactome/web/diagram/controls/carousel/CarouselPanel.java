package org.reactome.web.diagram.controls.carousel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.common.IconButton;

import java.util.LinkedList;
import java.util.List;

/**
 * This class implements the Carousel widget, providing an
 * easy way to show a series of images with accompanying text.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class CarouselPanel extends FlowPanel {

    private List<Slide> slidesList;
    private FlowPanel sliderPanel;
    private Button leftBtn;
    private Button rightBtn;
    private List<Button> circleButtons;

    private int currentSlideIndex;
    private int slideWidth;
    private int slideHeight;
    private String backColour;

    public CarouselPanel(List<Slide> slidesList, int slideWidth, int slideHeight) {
        this(slidesList, slideWidth, slideHeight, "transparent");
    }

    public CarouselPanel(List<Slide> slidesList, int slideWidth, int slideHeight, String backColour) {
        this.slidesList = slidesList;
        this.slideWidth = slideWidth;
        this.slideHeight = slideHeight;
        this.backColour = backColour;
        init();
    }

    private void init() {
        //Important: set the width of the widget equal
        // to the width of the slides
        setWidth(slideWidth + "px");

        ResourceCSS css = RESOURCES.getCSS();
        setStyleName(css.carouselPanel());
        getElement().getStyle().setBackgroundColor(backColour);

        sliderPanel = new FlowPanel();                      // Inner panel with the slides
        sliderPanel.setStyleName(css.sliderPanel());
        sliderPanel.setWidth(slidesList.size() * slideWidth + "px");
        populateSlides();

        FlowPanel sliderOuterPanel = new FlowPanel();        // Carousel outer panel
        sliderOuterPanel.setStyleName(css.carouselPanel());
        sliderOuterPanel.setWidth(slideWidth + "px");
        sliderOuterPanel.setHeight(slideHeight + "px");
        sliderOuterPanel.add(sliderPanel);

        leftBtn = new IconButton("", RESOURCES.leftIcon(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectSlide(slidesList.get(--currentSlideIndex));
            }
        });
        leftBtn.setStyleName(css.leftBtn());

        rightBtn = new IconButton("", RESOURCES.rightIcon(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectSlide(slidesList.get(++currentSlideIndex));
            }
        });
        rightBtn.setStyleName(css.rightBtn());

        FlowPanel controlsPanel = new FlowPanel();
        controlsPanel.setWidth(slideWidth + "px");
        controlsPanel.add(leftBtn);
        controlsPanel.add(rightBtn);
        controlsPanel.add(createButtons());

        add(sliderOuterPanel);
        add(controlsPanel);

        selectSlide(slidesList.get(0));
    }

    private void populateSlides() {
        for (Slide slide : slidesList) {
            sliderPanel.add(slide);
        }
    }

    private FlowPanel createButtons() {
        circleButtons = new LinkedList<>();
        FlowPanel buttonsPanel = new FlowPanel();           // contains the circle buttons
        buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        for (final Slide slide : slidesList) {
            Button btn = new Button("", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectSlide(slide);
                }
            });
            btn.setStyleName(RESOURCES.getCSS().circleBtn());
            buttonsPanel.add(btn);
            circleButtons.add(btn);
        }
        return buttonsPanel;
    }

    protected void selectSlide(Slide slide) {
        slide.init(slideWidth, slideHeight);
        currentSlideIndex = slidesList.indexOf(slide);
        sliderPanel.getElement().getStyle().setMarginLeft(-currentSlideIndex * slideWidth, Style.Unit.PX);

        // Enable/disable left and right buttons accordingly
        if (currentSlideIndex == 0) {
            leftBtn.setEnabled(false);
            rightBtn.setEnabled(true);
        } else if (currentSlideIndex == slidesList.size() - 1) {
            leftBtn.setEnabled(true);
            rightBtn.setEnabled(false);
        } else {
            leftBtn.setEnabled(true);
            rightBtn.setEnabled(true);
        }

        // Set colour of the selected circle button
        if (circleButtons != null) {
            for (int i = 0; i < circleButtons.size(); i++) {
                if (currentSlideIndex == i) {
                    circleButtons.get(i).addStyleName(RESOURCES.getCSS().circleBtnSelected());
                } else {
                    circleButtons.get(i).removeStyleName(RESOURCES.getCSS().circleBtnSelected());
                }
            }
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/left.png")
        ImageResource leftIcon();

        @Source("images/right.png")
        ImageResource rightIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-CarouselPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/carousel/CarouselPanel.css";

        String carouselPanel();

        String sliderOuterPanel();

        String sliderPanel();

        String buttonsPanel();

        String leftBtn();

        String rightBtn();

        String circleBtn();

        String circleBtnSelected();
    }
}
