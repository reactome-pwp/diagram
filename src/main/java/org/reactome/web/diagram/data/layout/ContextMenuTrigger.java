package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class ContextMenuTrigger {

    private Coordinate a;
    private Coordinate b;
    private Coordinate c;

    ContextMenuTrigger(Coordinate a, Coordinate b, Coordinate c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public ContextMenuTrigger(NodeCommon node) {
        NodeProperties prop = node.getProp();
        if(node.getRenderableClass().equals("Gene")){
            this.a = CoordinateFactory.get(prop.getX() + prop.getWidth() - 5, prop.getY() + prop.getHeight() * 3/4);
            this.b = CoordinateFactory.get(prop.getX() + prop.getWidth() - 12, prop.getY() + prop.getHeight() * 3/4 - 4);
            this.c = CoordinateFactory.get(prop.getX() + prop.getWidth() - 12, prop.getY() + prop.getHeight() * 3/4 + 4);
        }else {
            this.a = CoordinateFactory.get(prop.getX() + prop.getWidth() - 5, prop.getY() + prop.getHeight() / 2);
            this.b = CoordinateFactory.get(prop.getX() + prop.getWidth() - 12, prop.getY() + prop.getHeight() / 2 - 4);
            this.c = CoordinateFactory.get(prop.getX() + prop.getWidth() - 12, prop.getY() + prop.getHeight() / 2 + 4);
        }
    }

    public boolean isHovered(Coordinate m) {
        Double s = a.getY() * c.getX() - a.getX() * c.getY() + (c.getY() - a.getY()) * m.getX() + (a.getX() - c.getX()) * m.getY();
        Double t = a.getX() * b.getY() - a.getY() * b.getX() + (a.getY() - b.getY()) * m.getX() + (b.getX() - a.getX()) * m.getY();

        if ((s < 0) != (t < 0)) return false;

        Double aux = -b.getY() * c.getX() + a.getY() * (c.getX() - b.getX()) + a.getX() * (b.getY() - c.getY()) + b.getX() * c.getY();
        if (aux < 0.0) {
            s = -s;
            t = -t;
            aux = -aux;
        }
        return s > 0 && t > 0 && (s + t) < aux;
    }

    public ContextMenuTrigger transform(double factor, Coordinate delta) {
        return new ContextMenuTrigger(
                a.transform(factor, delta),
                b.transform(factor, delta),
                c.transform(factor, delta)
        );
    }

    public Coordinate getA() {
        return a;
    }

    public Coordinate getB() {
        return b;
    }

    public Coordinate getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContextMenuTrigger trigger = (ContextMenuTrigger) o;

        if (a != null ? !a.equals(trigger.a) : trigger.a != null) return false;
        //noinspection SimplifiableIfStatement
        if (b != null ? !b.equals(trigger.b) : trigger.b != null) return false;
        return !(c != null ? !c.equals(trigger.c) : trigger.c != null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContextMenuTrigger{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
