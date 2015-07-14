package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeProperties;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class NodePropertiesFactory implements NodeProperties {
    private Double x;
    private Double y;
    private Double width;
    private Double height;

    private NodePropertiesFactory(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Double getX() {
        return x;
    }

    @Override
    public Double getY() {
        return y;
    }

    @Override
    public Double getWidth() {
        return width;
    }

    @Override
    public Double getHeight() {
        return height;
    }

    public static NodeProperties transform(NodeProperties prop, double factor, Coordinate delta) {
        return new NodePropertiesFactory(
                prop.getX() * factor + delta.getX(),
                prop.getY() * factor + delta.getY(),
                prop.getWidth() * factor,
                prop.getHeight() * factor
        );
    }
}
