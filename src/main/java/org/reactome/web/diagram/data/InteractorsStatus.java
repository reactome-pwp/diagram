package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.layout.SummaryItem;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsStatus {
    private Set<String> burstEntities;

    public InteractorsStatus() {
        this.burstEntities = new HashSet<>();
    }

    public void onBurstToggle(SummaryItem summaryItem, String identifier) {
        if (summaryItem!=null && summaryItem.getType().equals("TR")) {
            if (summaryItem.getPressed()) {
                burstEntities.add(identifier);
            } else {
                burstEntities.remove(identifier);
            }
        }
    }

    public boolean isVisible() {
        return !this.burstEntities.isEmpty();
    }
}
