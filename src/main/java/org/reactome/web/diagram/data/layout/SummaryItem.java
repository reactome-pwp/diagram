package org.reactome.web.diagram.data.layout;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface SummaryItem {

    String getType();

    Shape getShape();

    Boolean getPressed();

    void setPressed(boolean pressed);

    Integer getNumber();

    void setNumber(Integer number);
}
