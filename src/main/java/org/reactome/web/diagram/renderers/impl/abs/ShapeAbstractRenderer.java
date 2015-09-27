package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ShapeAbstractRenderer {

    //The last parameter is meant to have a way of disabling the default filling for views where the shape
    //filling is hardly appreciated for the human eye so avoiding the filling speeds the rendering up
    public static void draw(AdvancedContext2d ctx, Shape shape, Double factor, Coordinate offset) {
        if (shape == null) return;
        shape = ShapeFactory.transform(shape, factor, offset);
        String type = shape.getType();
        double x=0, y=0;
        switch (type) {
            case "ARROW":
                ctx.beginPath();
                ctx.moveTo(shape.getA().getX(), shape.getA().getY());
                ctx.lineTo(shape.getB().getX(), shape.getB().getY());
                ctx.lineTo(shape.getC().getX(), shape.getC().getY());
                ctx.closePath();
                ctx.stroke();
                fill(ctx, shape.getEmpty());
                break;
            case "BOX":
                x = shape.getA().getX() + (shape.getB().getX() - shape.getA().getX())/ 2.0;
                y = shape.getA().getY() + (shape.getB().getY() - shape.getA().getY())/ 2.0;
                ctx.beginPath();
                ctx.rect(
                        shape.getA().getX(),
                        shape.getA().getY(),
                        shape.getB().getX() - shape.getA().getX(),
                        shape.getB().getY() - shape.getA().getY()
                );
                ctx.stroke();
                fill(ctx, shape.getEmpty());
                break;
            case "CIRCLE":
                x = shape.getC().getX(); y = shape.getC().getY();
                ctx.beginPath();
                ctx.arc(
                        shape.getC().getX(),
                        shape.getC().getY(),
                        shape.getR(),
                        0,
                        2 * Math.PI
                );
                ctx.stroke();
                fill(ctx, shape.getEmpty());
                break;
            case "DOUBLE_CIRCLE":
                x = shape.getC().getX(); y = shape.getC().getY();
                ctx.beginPath();
                ctx.arc(
                        shape.getC().getX(),
                        shape.getC().getY(),
                        shape.getR(),
                        0,
                        2 * Math.PI
                );
                ctx.stroke();
                fill(ctx, shape.getEmpty());
                ctx.beginPath();
                ctx.arc(
                        shape.getC().getX(),
                        shape.getC().getY(),
                        shape.getR1(),
                        0,
                        2 * Math.PI
                );
                ctx.stroke();
                break;
            case "STOP":
                ctx.beginPath();
                ctx.moveTo(shape.getA().getX(), shape.getA().getY());
                ctx.lineTo(shape.getB().getX(), shape.getB().getY());
                ctx.stroke();
                break;
            default:
                throw new RuntimeException("Do not know shape " + type);
        }

        if(shape.getS()!=null){
            ctx.save();
            ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
            ctx.setTextAlign(Context2d.TextAlign.CENTER);
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
            ctx.setFillStyle(ctx.getStrokeStyle());
            ctx.fillText(shape.getS(),x, y);
            ctx.restore();
        }
    }

    private static void fill(AdvancedContext2d ctx, Boolean isEmpty) {
        ctx.save();
        if(isEmpty==null){  // then it is probably false
            ctx.setFillStyle(ctx.getStrokeStyle());
            ctx.fill();
        }else{              // Change the color to white and fill the shape
            ctx.setFillStyle("#FFFFFF");
            ctx.fill();
        }
        ctx.restore();
    }
}
