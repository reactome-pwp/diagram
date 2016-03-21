package org.reactome.web.diagram.common;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBoxBase;
import org.reactome.web.diagram.common.validation.AbstractValidator;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InputPanel<T extends TextBoxBase, V extends AbstractValidator> extends FlowPanel {

    private Label titleLb;
    private T input;
    private V validator;

    public InputPanel(String title, T T, V V, String style, String titleStyle, String inputStyle) {
        setStyleName(style);
        titleLb = new Label(title);
        titleLb.setStyleName(titleStyle);

        input = T;
        input.setStyleName(inputStyle);

        validator = V;

        add(titleLb);
        add(input);
        add(validator);
    }



    public String getText() {
        return input.getText();
    }

    public void setHintMessage(String tip) {
        input.getElement().setPropertyString("placeholder", tip);
    }

    public void setText(String text) {
        input.setText(text);
    }

    public void setReaOnly(boolean isReadOnly) {
        input.setReadOnly(isReadOnly);
    }

    public boolean validate(){
            return validator.validate(input.getText()) ? validator != null : false;
    }

    public void clear() {
        if(validator!=null) {
            validator.clear();
        }
    }
}
