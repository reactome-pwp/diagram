package org.reactome.web.diagram.data.interactors.custom;

import org.reactome.web.diagram.data.interactors.custom.model.CustomResource;
import org.reactome.web.diagram.data.interactors.custom.model.CustomResources;
import org.reactome.web.diagram.data.interactors.custom.model.factory.StoredResourcesModelException;
import org.reactome.web.diagram.data.interactors.custom.model.factory.StoredResourcesModelFactory;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.storage.StorageSolutionFactory;
import org.reactome.web.diagram.util.storage.solutions.StorageSolution;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("Duplicates")
public class ResourcesManager {
    private static final String CUSTOM_RESOURCES_KEY = "customResources";

    private static ResourcesManager instance;
    private StorageSolution storageSolution;

    private List<CustomResource> resources;

    private ResourcesManager() {
        storageSolution = StorageSolutionFactory.getStorage();
        this.resources = loadResources(CUSTOM_RESOURCES_KEY);
    }

    public static void initialise(){
        if(instance != null) {
            throw new RuntimeException("Resources Manager has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        instance = new ResourcesManager();
    }

    public static ResourcesManager get(){
        if (instance == null) {
            throw new RuntimeException("Resources Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return instance;
    }

    public List<CustomResource> getResources() {
        return resources;
    }

    public void createAndAddResource(String name, String token){
        try {
            CustomResource cr = StoredResourcesModelFactory.create(CustomResource.class);
            cr.setName(name);
            cr.setToken(token);

            resources.add(cr);
            saveResources(CUSTOM_RESOURCES_KEY, resources);
        } catch (StoredResourcesModelException e) {
            e.printStackTrace();
        }
    }

    public void deleteResource(String token) {
        CustomResource toBeDeleted = null;
        for (CustomResource resource : resources) {
            if(resource.getToken().equals(token)) {
                toBeDeleted = resource;
                break;
            }
        }
        if(toBeDeleted!=null) {
            resources.remove(toBeDeleted);
            saveResources(CUSTOM_RESOURCES_KEY, resources);
        }
    }

    public void deleteAllResources() {
        resources = new LinkedList<>();
        saveResources(CUSTOM_RESOURCES_KEY, resources);
    }

    /***
     * Load all locally stored resources (if any)
     */
    private List<CustomResource> loadResources(String key){
        List<CustomResource> rtn = new LinkedList<>();

        if(storageSolution!=null) {
            String json = storageSolution.read(key);
            if(json != null) {
                try {
                    CustomResources resources = StoredResourcesModelFactory.getModelObject(CustomResources.class, json);
                    rtn.addAll(resources.getCustomResources());
                    Console.info("Loaded " + rtn.size() + " custom resources.");
                } catch (StoredResourcesModelException e) {
                    //TODO propagate the exception upwards
                    Console.info("Failed to load locally stored custom resources");
                    e.printStackTrace();
                }
            }
        }
        return rtn;
    }

    /***
     * Persist resources to local storage
     */
    private void saveResources(String key, List<CustomResource> resources) {
        if(storageSolution!=null) {
            try {
                CustomResources container = StoredResourcesModelFactory.create(CustomResources.class);
                container.setCustomResources(resources);

                String json = StoredResourcesModelFactory.serialiseModelObject(container);
                storageSolution.write(key, json);
            } catch (StoredResourcesModelException e) {
                e.printStackTrace();
            }
        }
    }
}
