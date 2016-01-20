package org.reactome.web.diagram.data.interactors.raw.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import org.reactome.web.diagram.data.interactors.raw.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class InteractorsFactory {

    @SuppressWarnings("UnusedDeclaration")
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<RawResource> resource();
        AutoBean<Synonym> synonyms();
        AutoBean<RawInteractor> interactor();
        AutoBean<RawInteractorEntity> entityInteractors();
        AutoBean<RawInteractors> diagramInteractors();
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