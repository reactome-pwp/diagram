package org.reactome.web.diagram.data.interactors.raw.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.data.interactors.raw.DiagramInteractors;
import org.reactome.web.diagram.data.interactors.raw.EntityInteractor;
import org.reactome.web.diagram.data.interactors.raw.Interactor;
import org.reactome.web.diagram.data.interactors.raw.Synonym;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorsFactory {

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<Synonym> synonyms();
        AutoBean<Interactor> interactor();
        AutoBean<EntityInteractor> entityInteractors();
        AutoBean<DiagramInteractors> diagramInteractors();
    }

    public static <T> T getInteractorObject(Class<T> cls, String json) throws InteractorsException {
        try {
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        } catch (Throwable e) {
            throw new InteractorsException();
        }
    }
}