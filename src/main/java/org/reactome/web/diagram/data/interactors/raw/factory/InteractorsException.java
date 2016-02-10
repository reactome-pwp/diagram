package org.reactome.web.diagram.data.interactors.raw.factory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsException extends Exception {

    private String resource;

    public InteractorsException() {}

    public InteractorsException(String resource, String message) {
        super(message);
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
