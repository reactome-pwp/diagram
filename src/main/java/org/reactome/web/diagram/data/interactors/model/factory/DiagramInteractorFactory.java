package org.reactome.web.diagram.data.interactors.model.factory;

import org.reactome.web.diagram.data.DiagramContent;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramInteractorFactory {

    private Map<String, InteractorEntity> cache = new HashMap<>();

    public List<DiagramInteractor> createInteractors(DiagramContent content, RawInteractors rawInteractors) {
        List<DiagramInteractor> rtn = new ArrayList<>();

        for (RawInteractorEntity interactorEntity : rawInteractors.getEntities()) {
            for (RawInteractor rawInteractor : interactorEntity.getInteractors()) {
                InteractorEntity ie = new InteractorEntity(rawInteractor);
            }



        }

        return rtn;
    }
}
