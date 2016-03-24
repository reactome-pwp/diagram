package org.reactome.web.diagram.context.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.common.InputPanel;
import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.common.validation.ContentValidator;
import org.reactome.web.diagram.common.validation.FileValidator;
import org.reactome.web.diagram.common.validation.NameValidator;
import org.reactome.web.diagram.common.validation.UrlValidator;
import org.reactome.web.diagram.data.interactors.custom.raw.RawUploadError;
import org.reactome.web.diagram.data.interactors.custom.raw.RawUploadResponse;
import org.reactome.web.diagram.util.Console;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class InsertItemDialog extends PopupPanel implements CustomResourceSubmitter.Handler,
        ValueChangeHandler, ClickHandler, SelectionHandler<Integer> {

    public interface Handler {
        void onResourceAdded(String name, String token);
    }

    private static final String SERVICE_URL_ACTION = "/ContentService/interactors/upload/tuple/url?name=";
    private static final String TUPLE_URL_ACTION = "/ContentService/interactors/upload/tuple/url?name=";
    private static final String TUPLE_CONTENT_ACTION = "/ContentService/interactors/upload/tuple/content?name=";
    private static final String TUPLE_FILE_ACTION = "/ContentService/interactors/upload/tuple/form?name=";

    private static final String URL = "url";
    private static final String FILE = "file";
    private static final String CONTENT = "content";

    private CustomResourceSubmitter submitter;

    private InputPanel nameInput;
    private InputPanel urlInput;
    private InputPanel fileInput;
    private InputPanel copyPasteInput;
    private InputPanel urlServiceInput;

    private FlowPanel errorPanel;
    private Label errorTitle;

    private FileUpload fileUpload;
    private FormPanel formPanel;
    private TabLayoutPanel tabPanel;

    private List<Button> tabButtons = new LinkedList<>();
    private Button addDataTabBtn;
    private Button addServiceTabBtn;

    private Button submitBtn;
    private Button cancelBtn;

    private WarningsReport warningsReport;
    private String selectedOption;
    private Handler handler;

    public InsertItemDialog(Handler handler) {
        super();
        this.handler = handler;
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);
        this.setStyleName(RESOURCES.getCSS().popupPanel());

        submitter = new CustomResourceSubmitter(this);
        warningsReport = new WarningsReport();

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
        IconButton btn = (IconButton) event.getSource();
        int selectedIndex = tabPanel.getSelectedIndex();
        boolean validationResult;
        if(btn.equals(submitBtn)) {
            validationResult = nameInput.validate();
            String name = nameInput.getText().trim();
            if(selectedIndex == 0) {        //Tuple tab
                switch (selectedOption) {
                    case URL:
                        validationResult = validationResult && urlInput.validate();
                        if (validationResult) submitter.submit(urlInput.getText(), TUPLE_URL_ACTION + name);
                        break;
                    case FILE:
                        validationResult = validationResult && fileInput.validate();
                        if (validationResult) submitter.submit(formPanel, TUPLE_FILE_ACTION + name);
                        break;
                    case CONTENT:
                        validationResult = validationResult && copyPasteInput.validate();
                        if (validationResult) submitter.submit(copyPasteInput.getText(), TUPLE_CONTENT_ACTION + name);
                        break;
                }
            } else {                        //Service tab
                validationResult = validationResult && urlServiceInput.validate();
                if (validationResult) submitter.submit(urlServiceInput.getText(), SERVICE_URL_ACTION + name);
            }

        } else if (btn.equals(cancelBtn)) {
            submitter.cancel();
            this.hide();
        }
    }

    @Override
    public void onSelection(SelectionEvent event) {
        Integer index = (Integer) event.getSelectedItem();
        for (int i=0;i<tabButtons.size();i++) {
            Button btn = tabButtons.get(i);
            if(index.equals(i)) {
                btn.addStyleName(RESOURCES.getCSS().tabButtonSelected());
            } else {
                btn.removeStyleName(RESOURCES.getCSS().tabButtonSelected());
            }
        }
    }

    @Override
    public void onSubmission() {
        showErrors(false);
        showLoading(true);
    }

    @Override
    public void onSubmissionCompleted(RawUploadResponse response, long time) {
        showLoading(false);
        this.hide();
        List<String> warnings = response.getWarningMessages();
        if(warnings!=null && !warnings.isEmpty()) {
            Console.info(warnings);
            warningsReport.setTitle("Successful submission with warnings (" + warnings.size() + ")");
            warningsReport.setMessages(warnings);
            warningsReport.show();
        } else {
            Console.info("New Token:" + response.getSummary().getToken());
        }
        handler.onResourceAdded(nameInput.getText().trim(), response.getSummary().getToken());
    }

    @Override
    public void onSubmissionException(String message) {
        showLoading(false);
        errorTitle.setText(message);
        showErrors(true);
    }

    @Override
    public void onSubmissionError(RawUploadError error) {
        showLoading(false);
        errorTitle.setText(error.getReason() + " (" + error.getCode() + ")");
        showErrors(true);
    }

    @Override
    public void onValueChange(ValueChangeEvent event) {
        RadioButton selectedBtn = (RadioButton) event.getSource();
        selectedOption = selectedBtn.getFormValue(); // Keep current selection
        urlInput.setVisible(false);
        formPanel.setVisible(false);
        fileInput.setVisible(false);
        copyPasteInput.setVisible(false);
        switch (selectedOption) {
            case URL:
                urlInput.setVisible(true);
                break;
            case FILE:
                formPanel.setVisible(true);
                fileInput.setVisible(true);
                break;
            case CONTENT:
                copyPasteInput.setVisible(true);
                break;
        }
    }

    private void initUI() {
        ResourceCSS css = RESOURCES.getCSS();
        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(css.containerPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        nameInput = new InputPanel("Name:", new TextBox(), new NameValidator(), css.namePanel(), css.infoLabel(), css.inputTB(), css.explanation());
        nameInput.setHintMessage("Enter the name of your resource");

        RadioButton urlBtn = new RadioButton("UploadOption", "URL");
        urlBtn.setFormValue(URL); //use FormValue to keep the value
        urlBtn.setTitle("Provide the URL of your data");
        urlBtn.setStyleName(css.uploadOptionBtn());
        urlBtn.addValueChangeHandler(this);
        urlBtn.setValue(true); selectedOption = URL;
        RadioButton fileBtn = new RadioButton("UploadOption", "File");
        fileBtn.setFormValue(FILE); //use FormValue to keep the value
        fileBtn.setTitle("Provide the file of your data");
        fileBtn.setStyleName(css.uploadOptionBtn());
        fileBtn.addValueChangeHandler(this);
        RadioButton pasteBtn = new RadioButton("UploadOption", "Copy & Paste");
        pasteBtn.setFormValue(CONTENT); //use FormValue to keep the value
        pasteBtn.setTitle("Copy and paste your data directly");
        pasteBtn.setStyleName(css.uploadOptionBtn());
        pasteBtn.addValueChangeHandler(this);
        FlowPanel uploadOptionsPanel = new FlowPanel();
        uploadOptionsPanel.setStyleName(css.rowPanel());
        uploadOptionsPanel.add(urlBtn);
        uploadOptionsPanel.add(fileBtn);
        uploadOptionsPanel.add(pasteBtn);

        urlInput = new InputPanel("URL:", new TextBox(), new UrlValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        urlInput.setHintMessage("Enter the URL of your data");
        urlInput.setExplanation("this is the URL explanation");

        fileInput = new InputPanel("File:", new TextBox(), new FileValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        fileInput.setHintMessage("Click here to choose your file");
        fileInput.setExplanation("this is the file explanation");
        fileInput.setReaOnly(true);
        fileInput.setVisible(false);

        formPanel = getFormPanel();
        formPanel.setVisible(false);

        copyPasteInput = new InputPanel("Paste:", new TextArea(), new ContentValidator(), css.rowPanel(), css.infoLabel(), css.textArea(), css.explanation());
        copyPasteInput.setHintMessage("Copy & paste your data here e.g. lalala");
        copyPasteInput.setExplanation("this is the content explanation");
        copyPasteInput.setVisible(false);

        FlowPanel addDataFP = new FlowPanel();
        addDataFP.setStyleName(css.addDataPanel());
        addDataFP.add(uploadOptionsPanel);
        addDataFP.add(urlInput);
        addDataFP.add(formPanel);
        addDataFP.add(fileInput);
        addDataFP.add(copyPasteInput);

        urlServiceInput = new InputPanel("URL:", new TextBox(), new UrlValidator(), css.rowPanel(), css.infoLabel(), css.inputTB(), css.explanation());
        urlServiceInput.setHintMessage("Enter the URL of your PSICQUIC service");
        urlServiceInput.setExplanation("this is the service url explanation");

        FlowPanel addServiceFP = new FlowPanel();
        addServiceFP.setStyleName(css.addServicePanel());
//        addServiceFP.add(urlServiceInput);
        addServiceFP.add(new Label("Under construction - Coming soon."));

        tabPanel = new TabLayoutPanel(4, Style.Unit.EM);
        tabPanel.setStyleName(css.tabPanel());
        tabPanel.add(addDataFP, addDataTabBtn = getButton("Add your data", RESOURCES.submitNormal()));
        tabPanel.add(addServiceFP, addServiceTabBtn = getButton("Add a custom service", RESOURCES.submitNormal()));
        tabPanel.addSelectionHandler(this);
        tabPanel.selectTab(0);
        addDataTabBtn.addStyleName(css.tabButtonSelected());

        submitBtn = new IconButton("Submit", RESOURCES.submitNormal());
        submitBtn.setStyleName(css.submitBtn());
        submitBtn.addClickHandler(this);
        cancelBtn = new IconButton("Cancel", RESOURCES.cancelNormal());
        cancelBtn.setStyleName(css.submitBtn());
        cancelBtn.addClickHandler(this);

        Image errorImage = new Image(RESOURCES.headerIcon());
        errorTitle = new Label("This is the Error Title");
        FlowPanel errorText = new FlowPanel();
        errorText.setStyleName(css.errorText());
        errorText.add(errorTitle);

        errorPanel = new FlowPanel();
        errorPanel.setStyleName(css.errorPanel());
        errorPanel.add(errorImage);
        errorPanel.add(errorText);

        FlowPanel actionPanel = new FlowPanel();
        actionPanel.setStyleName(css.actionPanel());
        actionPanel.setVisible(true);
        actionPanel.add(submitBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(errorPanel);

        vp.add(nameInput);
        vp.add(tabPanel);
        vp.add(actionPanel);
        this.add(vp);

        showLoading(false);
        showErrors(false);
    }

    private Widget setTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        Label title = new Label("Add a new resource");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new PwpButton("Close this dialog", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                InsertItemDialog.this.hide();
            }
        });
        header.add(image);
        header.add(title);
        header.add(closeBtn);
        return header;
    }

    public Button getButton(String text, ImageResource imageResource){
        Image buttonImg = new Image(imageResource);
        Label buttonLbl = new Label(text);

        FlowPanel fp = new FlowPanel();
        fp.add(buttonImg);
        fp.add(buttonLbl);

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        Button btn = new Button(safeHtml);
        btn.setStyleName(RESOURCES.getCSS().tabButton());
        tabButtons.add(btn);
        return btn;
    }

    private FormPanel getFormPanel(){
        fileUpload = new FileUpload();
        fileUpload.setStyleName(RESOURCES.getCSS().fileUpload());
        fileUpload.setName("file");
        fileUpload.setTitle("Select a file to analyse");
        fileUpload.setEnabled(true);
        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String filename = fileUpload.getFilename();
                if(filename!=null && !filename.isEmpty()) {
                    if(filename.contains("\\")) {
                        fileInput.setText(filename.substring(fileUpload.getFilename().lastIndexOf("\\") + 1));
                    } else {
                        fileInput.setText(filename);
                    }
                } else {
                    fileInput.setText("");
                }
            }
        });
//        fileUpload.getElement().setAttribute("accept", ".txt");
        FormPanel form = new FormPanel();
        form.addSubmitHandler(submitter);
        form.addSubmitCompleteHandler(submitter);
        form.add(fileUpload);
        return form;
    }

    private void showLoading(boolean loading){
        if(loading) {
            ((IconButton) submitBtn).setImage(RESOURCES.loader());
        } else {
            ((IconButton) submitBtn).setImage(RESOURCES.submitNormal());
        }
    }

    private void showErrors(boolean showErrors){
        errorPanel.setVisible(showErrors);
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

        @Source("images/addNewResources.png")
        ImageResource headerIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/ok.png")
        ImageResource submitNormal();

        @Source("images/cancel.png")
        ImageResource cancelNormal();

        @Source("images/loader.gif")
        ImageResource loader();
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

        String containerPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String namePanel();

        String rowPanel();

        String actionPanel();

        String tabPanel();

        String tabButton();

        String tabButtonSelected();

        String addDataPanel();

        String addServicePanel();

        String infoLabel();

        String inputTB();

        String uploadOptionBtn();

        String fileUpload();

        String textArea();
        
        String explanation();

        String submitBtn();

        String errorPanel();

        String errorText();
    }
}
