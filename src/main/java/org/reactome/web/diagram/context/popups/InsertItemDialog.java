package org.reactome.web.diagram.context.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.util.Console;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class InsertItemDialog extends PopupPanel implements ClickHandler, FormPanel.SubmitHandler, FormPanel.SubmitCompleteHandler, ValueChangeHandler {

    private static final String FORM_ACTION = "/AnalysisService/identifiers/form?page=1";
    private static final String URL = "url";
    private static final String FILE = "file";
    private static final String COPY_PASTE = "copyPaste";

    private FlowPanel urlPanel;
    private FlowPanel filePanel;
    private FlowPanel copyPanel;

    private TextBox nameTB;
    private TextBox urlTB;
    private FileUpload fileUpload;
    private FormPanel form;

    private Button submitBtn;
    private Button cancelBtn;

    private String selectedOption;

    public InsertItemDialog() {
        super();
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);
        this.addStyleName(RESOURCES.getCSS().popupPanel());

        initUI();
    }

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
    }

    @Override
    public void show() {
        super.show();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                center();
            }
        });
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if(btn.equals(submitBtn)) {
            // Validate and submit the form;
            form.submit();
        } else if (btn.equals(cancelBtn)) {
            this.hide();
        }
    }

    @Override
    public void onSubmit(FormPanel.SubmitEvent event) {

    }

    @Override
    public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {

    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        selectedOption = selectedBtn.getFormValue(); // Keep current selection
        urlPanel.setVisible(false);
        filePanel.setVisible(false);
        copyPanel.setVisible(false);
        switch (selectedOption) {
            case URL:
                urlPanel.setVisible(true);
                break;
            case FILE:
                form.setAction(FORM_ACTION);
                filePanel.setVisible(true);
                break;
            case COPY_PASTE:
                copyPanel.setVisible(true);
                break;
        }
    }

    private void initUI() {
        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(RESOURCES.getCSS().analysisPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        Label nameLabel = new Label("Name:");
        nameLabel.setStyleName(RESOURCES.getCSS().infoLabel());
        nameTB = new TextBox();
        nameTB.setStyleName(RESOURCES.getCSS().inputTB());
        nameTB.getElement().setPropertyString("placeholder", "Enter the name of your resource");
        FlowPanel namePanel = new FlowPanel();
        namePanel.setStyleName(RESOURCES.getCSS().namePanel());
        namePanel.add(nameLabel);
        namePanel.add(nameTB);

        RadioButton urlBtn = new RadioButton("UploadOption", "URL");
        urlBtn.setFormValue(URL); //use FormValue to keep the value
        urlBtn.setTitle("Provide a URL for your resource");
        urlBtn.setStyleName(RESOURCES.getCSS().uploadOptionBtn());
        urlBtn.setValue(true);
        urlBtn.addValueChangeHandler(this);
        RadioButton fileBtn = new RadioButton("UploadOption", "File");
        fileBtn.setFormValue(FILE); //use FormValue to keep the value
        fileBtn.setTitle("Provide a file for your resource");
        fileBtn.setStyleName(RESOURCES.getCSS().uploadOptionBtn());
        fileBtn.addValueChangeHandler(this);
        RadioButton pasteBtn = new RadioButton("UploadOption", "Copy & Paste");
        pasteBtn.setFormValue(COPY_PASTE); //use FormValue to keep the value
        pasteBtn.setTitle("Copy and paste your data");
        pasteBtn.setStyleName(RESOURCES.getCSS().uploadOptionBtn());
        pasteBtn.addValueChangeHandler(this);
        FlowPanel uploadOptionsPanel = new FlowPanel();
        uploadOptionsPanel.setStyleName(RESOURCES.getCSS().rowPanel());
        uploadOptionsPanel.add(urlBtn);
        uploadOptionsPanel.add(fileBtn);
        uploadOptionsPanel.add(pasteBtn);

        Label urlLabel = new Label("URL:");
        urlLabel.setStyleName(RESOURCES.getCSS().infoLabel());
        urlTB = new TextBox();
        urlTB.setStyleName(RESOURCES.getCSS().inputTB());
        urlTB.getElement().setPropertyString("placeholder", "Enter the URL of your resource");
        urlPanel = new FlowPanel();
        urlPanel.setStyleName(RESOURCES.getCSS().rowPanel());
        urlPanel.add(urlLabel);
        urlPanel.add(urlTB);
        urlPanel.setVisible(true);

        Label fileLabel = new Label("File:");
        fileLabel.setStyleName(RESOURCES.getCSS().infoLabel());
        form = getFormPanel();
        filePanel = new FlowPanel();
        filePanel.setStyleName(RESOURCES.getCSS().rowPanel());
        filePanel.add(fileLabel);
        filePanel.add(form);
        filePanel.setVisible(false);

        Label copyLabel = new Label("Paste:");
        copyLabel.setStyleName(RESOURCES.getCSS().infoLabel());
        copyPanel = new FlowPanel();
        copyPanel.setStyleName(RESOURCES.getCSS().rowPanel());
        copyPanel.add(copyLabel);
        TextArea pasteTA = new TextArea();
        pasteTA.setStyleName(RESOURCES.getCSS().textArea());
        pasteTA.getElement().setPropertyString("placeholder", "Copy & paste your data here e.g. lalala");
        copyPanel.add(pasteTA);
        copyPanel.setVisible(false);

        submitBtn = new IconButton("Submit", RESOURCES.headerIcon());
        submitBtn.setStyleName(RESOURCES.getCSS().submitBtn());
        submitBtn.addClickHandler(this);
        cancelBtn = new IconButton("Cancel", RESOURCES.headerIcon());
        cancelBtn.setStyleName(RESOURCES.getCSS().submitBtn());
        cancelBtn.addClickHandler(this);
        FlowPanel actionPanel = new FlowPanel();
        actionPanel.setStyleName(RESOURCES.getCSS().actionPanel());
        actionPanel.setVisible(true);
        actionPanel.add(submitBtn);
        actionPanel.add(cancelBtn);

        vp.add(namePanel);
        vp.add(uploadOptionsPanel);
        vp.add(urlPanel);
        vp.add(filePanel);
        vp.add(copyPanel);
        vp.add(actionPanel);
        this.add(vp);
    }

    private Widget setTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        header.add(image);
        Label title = new Label("Add a new resource");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new PwpButton("Close this dialog", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                InsertItemDialog.this.hide();
            }
        });
        header.add(title);
        header.add(closeBtn);
        return header;
    }



    private FormPanel getFormPanel(){
        fileUpload = new FileUpload();
        fileUpload.setStyleName(RESOURCES.getCSS().fileUpload());
        fileUpload.setName("file");
        fileUpload.setTitle("Select a file to analyse");
        fileUpload.setEnabled(true);
//        fileUpload.getElement().setAttribute("accept", ".txt");

        FormPanel form = new FormPanel();
        form.setMethod(FormPanel.METHOD_POST);
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        Console.info( this.fileUpload.getElement().getInnerHTML());
        form.add(this.fileUpload);
        form.addSubmitHandler(this);
        form.addSubmitCompleteHandler(this);
        return form;
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

        @Source("images/header_icon.png")
        ImageResource headerIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/download_normal.png")
        ImageResource downloadNormal();

        @Source("images/upload_normal.png")
        ImageResource uploadNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-InsertItemDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/InsertItemDialog.css";

        String popupPanel();

        String analysisPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String downloadPNG();

        String namePanel();

        String rowPanel();

        String actionPanel();

        String infoLabel();

        String inputTB();

        String uploadOptionBtn();

        String fileUpload();

        String textArea();

        String submitBtn();
    }
}
