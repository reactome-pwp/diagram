package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.handlers.InteractorSelectedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorSelectedEvent extends GwtEvent<InteractorSelectedHandler> {
    public static final Type<InteractorSelectedHandler> TYPE = new Type<>();

    public enum ObjectType {
        INTERACTOR("http://identifiers.org/##RESOURCE##/##ID##"),
        INTERACTION("http://www.ebi.ac.uk/Tools/webservices/psicquic/view/main.xhtml?query=");

        String url;

        ObjectType(String url) {
            this.url = url;
        }
    }

    private ObjectType type;
    private String identifier;

    public InteractorSelectedEvent(DiagramInteractor interactor) {
        if(interactor instanceof InteractorEntity){
            InteractorEntity entity = (InteractorEntity) interactor;
            this.identifier = entity.getAccession();
            this.type = ObjectType.INTERACTOR;
        } else {
            InteractorLink link = (InteractorLink) interactor;
            this.identifier = link.getId();
            this.type = ObjectType.INTERACTION;
        }
    }

    public InteractorSelectedEvent(RawInteractor interactor, ObjectType type){
        this.type = type;
        switch (type){
            case INTERACTOR:
                identifier = interactor.getAcc();
                break;
            case INTERACTION:
                identifier = interactor.getId();
                break;
        }
    }

    @Override
    public Type<InteractorSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorSelectedHandler handler) {
        handler.onInteractorSelected(this);
    }

    public ObjectType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUrl() {
        String url = "";
        switch (type){
            case INTERACTION:
                url = type.url + identifier;
                break;
            case INTERACTOR:
                String acc = identifier;
                String[] text = acc.split(":");
                String resource;
                if (text.length > 1){
                    resource = text[0].toLowerCase();
                } else {
                    resource = "uniprot"; //Uniprot is the default one
                }
                url = type.url.replace("##RESOURCE##", resource).replace("##ID##", acc);
                break;
        }
        return url;
    }

    @Override
    public String toString() {
        return "InteractorSelectedEvent{" +
                "type=" + type +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
