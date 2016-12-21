package org.reactome.web.diagram.client.visualisers;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface Visualiser extends IsWidget{

//    void setContext(Content context);



    //ControlActions
    void fitDiagram(boolean animation);

    void zoomDelta(double deltaFactor);

    void padding(int dX, int dY);

    void exportView();

//
//    void flag(Collection<DiagramObject> items, Context context);
//
//    void halo(Collection<DiagramObject> items, Context context);
//
//    void highlight(HoveredItem hoveredItem, Context context);
//
//    void highlightInteractor(DiagramInteractor item, Context context);
//
//    void decorators(HoveredItem hoveredItem, Context context);
//
//    void select(List<DiagramObject> items, Context context);
//
//    void setIllustration(String url);
//
//    void resetIllustration();
//
//    void setCursor();
//
//    void clear();
//
//    void clearThumbnail();

}
