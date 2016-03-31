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

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class CarouselPanel extends FlowPanel {

    private List<Slide> slidesList;
    private FlowPanel sliderPanel;
    private FlowPanel buttonsPanel;
    private Button leftBtn;
    private Button rightBtn;

    private int currentSlideIndex;
    private int slideWidth;
    private int slideHeight;

    public CarouselPanel(List<Slide> slidesList, int slideWidth, int slideHeight) {
        this.slidesList = slidesList;
        this.slideWidth = slideWidth;
        this.slideHeight = slideHeight;
        init();
    }

    private void init() {
        ResourceCSS css = RESOURCES.getCSS();
        setStyleName(css.carouselPanel());

        sliderPanel = new FlowPanel();                      // Inner panel with the slides
        sliderPanel.setStyleName(css.sliderPanel());
        sliderPanel.setWidth(slidesList.size() * slideWidth + "px");
        populateSlides();

        FlowPanel sliderOuterPanel = new FlowPanel();        // Carousel outer panel
        sliderOuterPanel.setStyleName(css.carouselPanel());
        sliderOuterPanel.setWidth(slideWidth +"px");
        sliderOuterPanel.setHeight(slideHeight + "px");
        sliderOuterPanel.add(sliderPanel);

        createButtons();                                    // the circle buttons
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

        add(sliderOuterPanel);
        add(leftBtn);
        add(rightBtn);
        add(buttonsPanel);

        selectSlide(slidesList.get(0));
    }

    private void populateSlides() {
        for (Slide slide : slidesList) {
            sliderPanel.add(slide);
        }
    }

    private void createButtons(){
        buttonsPanel = new FlowPanel();           // contains the circle buttons
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
        }
    }

    protected void selectSlide(Slide slide){
        slide.init(slideWidth, slideHeight);
        currentSlideIndex = slidesList.indexOf(slide);
        sliderPanel.getElement().getStyle().setMarginLeft(-currentSlideIndex * slideWidth, Style.Unit.PX);
        if (currentSlideIndex == 0) {
            leftBtn.setEnabled(false);
            rightBtn.setEnabled(true);
        } else if (currentSlideIndex == slidesList.size()-1) {
            leftBtn.setEnabled(true);
            rightBtn.setEnabled(false);
        } else {
            leftBtn.setEnabled(true);
            rightBtn.setEnabled(true);
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
    }
}
