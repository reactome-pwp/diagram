package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Coordinate;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class CoordinateFactory implements Coordinate {
    private Double x;
    private Double y;

    private CoordinateFactory(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate get(double x, double y){
        return new CoordinateFactory(x, y);
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
    public Coordinate add(Coordinate value) {
        return new CoordinateFactory(x + value.getX(), y + value.getY());
    }

    @Override
    public Coordinate divide(double factor) {
        return new CoordinateFactory(x / factor, y / factor);
    }

    @Override
    public Coordinate minus(Coordinate value) {
        return new CoordinateFactory(x - value.getX(), y - value.getY());
    }

    @Override
    public Coordinate multiply(double factor) {
        return new CoordinateFactory(x * factor, y * factor);
    }

    @Override
    public Coordinate transform(double factor, Coordinate delta) {
        return new CoordinateFactory(
                x * factor + delta.getX(),
                y * factor + delta.getY()
        );
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
