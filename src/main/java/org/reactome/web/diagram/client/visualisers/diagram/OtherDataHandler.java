package org.reactome.web.diagram.client.visualisers.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.AdvancedContext2d;

public class OtherDataHandler {
    
    static interface OtherDataHandlerHelper {
        public void render(Collection<DiagramObject> items, AdvancedContext2d ctx);
    }
    
    private static OtherDataHandler handler;
    
    private List<OtherDataHandlerHelper> helpers;
    
    private OtherDataHandler() {
    }
    
    public static OtherDataHandler getHandler() {
        if (handler == null)
            handler = new OtherDataHandler();
        return handler;
    }
    
    public void registerHelper(OtherDataHandlerHelper helper) {
        if (helpers == null)
            helpers = new ArrayList<>();
        helpers.add(helper);
    }
    
    public void renderOtherData(Collection<DiagramObject> items, AdvancedContext2d ctx) {
        if (helpers == null)
            return;
        helpers.forEach(helper -> helper.render(items, ctx));
    }

}
