package org.reactome.web.diagram.util;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.renderers.impl.abs.DashedLineAbstractRenderer;

/**
 * This class extends the drawing functionality of the GWT Context2d.
 * Methods include drawing dashed lines and shapes, ellipses, arrows
 * and shapes specific to the Reactome diagram viewer.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AdvancedContext2d extends Context2d {

    protected AdvancedContext2d() {
    }

    //////////////////////////////
    //  Methods for Rectangles  //
    //////////////////////////////

    /**
     * Draws a simple rectangle.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param needsFill
     * @param needsStroke
     */
    public final void drawRectangle(double x,
                                    double y,
                                    double width,
                                    double height,
                                    boolean needsFill,
                                    boolean needsStroke) {
        double right = x + width;
        double bottom = y + height;

        beginPath();
        moveTo(x, y);
        lineTo(right, y);
        lineTo(right, bottom);
        lineTo(x, bottom);
        lineTo(x, y);
        closePath();

        if (needsFill) {
            fill();
        }
        if (needsStroke) {
            stroke();
        }
    }

    /**
     * Draws a dashed rectangle.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param dashedLinePattern
     * @param needsFill
     */
    @Deprecated
    public final void drawDashedRectangle(double x,
                                          double y,
                                          double width,
                                          double height,
                                          double[] dashedLinePattern,
                                          boolean needsFill) {
        if (needsFill) {
            drawRectangle(x, y, width, height, true, false);
        }

        double right = x + width;
        double bottom = y + height;

        // Draw four dashed lines
        drawDashedLine(x, y, right, y, dashedLinePattern);
        drawDashedLine(right, y, right, bottom, dashedLinePattern);
        drawDashedLine(right, bottom, x, bottom, dashedLinePattern);
        drawDashedLine(x, bottom, x, y, dashedLinePattern);
    }


    //////////////////////////////////////
    //  Methods for Rounded Rectangles  //
    //////////////////////////////////////

    public final void roundedRectangle(double x,
                                       double y,
                                       double width,
                                       double height,
                                       double arcWidth) {
        double right = x + width;
        double bottom = y + height;

        beginPath();
        moveTo(x + arcWidth, y);
        lineTo(right - arcWidth, y);
        quadraticCurveTo(right, y, right, y + arcWidth);
        lineTo(right, bottom - arcWidth);
        quadraticCurveTo(right, bottom, right - arcWidth, bottom);
        lineTo(x + arcWidth, bottom);
        quadraticCurveTo(x, bottom, x, bottom - arcWidth);
        lineTo(x, y + arcWidth);
        quadraticCurveTo(x, y, x + arcWidth, y);
//        closePath();
    }

    /**
     * Draws a rectangle with rounded edges.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param needsFill
     * @param needsStroke
     */
    @Deprecated
    public final void drawRoundedRectangle(double x,
                                           double y,
                                           double width,
                                           double height,
                                           double arcWidth,
                                           boolean needsFill,
                                           boolean needsStroke) {
        double right = x + width;
        double bottom = y + height;

        beginPath();
        moveTo(x + arcWidth, y);
        lineTo(right - arcWidth, y);
        quadraticCurveTo(right, y, right, y + arcWidth);
        lineTo(right, bottom - arcWidth);
        quadraticCurveTo(right, bottom, right - arcWidth, bottom);
        lineTo(x + arcWidth, bottom);
        quadraticCurveTo(x, bottom, x, bottom - arcWidth);
        lineTo(x, y + arcWidth);
        quadraticCurveTo(x, y, x + arcWidth, y);
        closePath();

        if (needsFill) {
            fill();
        }
        if (needsStroke) {
            stroke();
        }
    }

    /**
     * Draws a dashed rectangle with rounded edges.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param dashedLinePattern
     */
    public final void dashedRoundedRectangle(double x,
                                             double y,
                                             double width,
                                             double height,
                                             double arcWidth,
                                             double[] dashedLinePattern ){
        // Draw the four dashed lines
        double dashLength = dashedLinePattern[0];
        double gapLength = dashedLinePattern[1];
        beginPath();
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth + gapLength, y, x + width - arcWidth, y, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width, y + arcWidth, x + width, y + height - arcWidth, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width - arcWidth, y + height, x + arcWidth, y + height, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x, y + height - arcWidth, x, y + arcWidth, dashLength, gapLength);

        // Need to draw rounded corners
        moveTo(x, y + arcWidth);
        quadraticCurveTo(x, y, x + arcWidth, y);

        moveTo(x + width - arcWidth, y);
        quadraticCurveTo(x + width, y, x + width, y + arcWidth);

        moveTo(x + width, y + height - arcWidth);
        quadraticCurveTo(x + width, y + height, x + width - arcWidth, y + height);

        moveTo(x + arcWidth, y + height);
        quadraticCurveTo(x, y + height, x, y + height - arcWidth);
    }


    //////////////////////////////
    //  Methods for Octagons    //
    //////////////////////////////

    public final void octagon(double x,
                              double y,
                              double width,
                              double height,
                              double arcWidth) {

        double x1 = x + arcWidth;
        double y1 = y;

        beginPath();
        moveTo(x1, y1);
        x1 += (width - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width;
        y1 = y + arcWidth;
        lineTo(x1, y1);
        y1 += (height - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width - arcWidth;
        y1 = y + height;
        lineTo(x1, y1);
        x1 = x + arcWidth;
        lineTo(x1, y1);
        x1 = x;
        y1 = y + height - arcWidth;
        lineTo(x1, y1);
        y1 = y + arcWidth;
        lineTo(x1, y1);
        closePath();
    }

    /**
     * Draws an octagon - used to draw complexes.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param needsFill
     * @param needsStroke
     */
    @Deprecated
    public final void drawOctagon(double x,
                                  double y,
                                  double width,
                                  double height,
                                  double arcWidth,
                                  boolean needsFill,
                                  boolean needsStroke) {

        double x1 = x + arcWidth;
        double y1 = y;

        beginPath();
        moveTo(x1, y1);
        x1 += (width - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width;
        y1 = y + arcWidth;
        lineTo(x1, y1);
        y1 += (height - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width - arcWidth;
        y1 = y + height;
        lineTo(x1, y1);
        x1 = x + arcWidth;
        lineTo(x1, y1);
        x1 = x;
        y1 = y + height - arcWidth;
        lineTo(x1, y1);
        y1 = y + arcWidth;
        lineTo(x1, y1);
        closePath();

        if (needsFill) {
            fill();
        }
        if (needsStroke) {
            stroke();
        }
    }

    /**
     * Draws a dashed octagon - used to depict complexes.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param arcWidth
     * @param dashedLinePattern
     */
    public final void dashedOctagon(double x,
                                    double y,
                                    double width,
                                    double height,
                                    double arcWidth,
                                    double[] dashedLinePattern) {

        double dashLength = dashedLinePattern[0];
        double gapLength = dashedLinePattern[1];
        beginPath();
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth, y, x + width - arcWidth, y, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width, y + arcWidth, x + width, y + height - arcWidth, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth, y + height, x + width - arcWidth, y + height, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x, y + arcWidth, x, y + height - arcWidth, dashLength, gapLength);

        moveTo(x, y + arcWidth);
        lineTo(x + arcWidth, y);

        moveTo(x + width - arcWidth, y);
        lineTo(x + width, y + arcWidth);

        moveTo(x + width, y + height - arcWidth);
        lineTo(x + width - arcWidth, y + height);

        moveTo(x + arcWidth, y + height);
        lineTo(x, y + height - arcWidth);
    }

    ////////////////////////////////////
    //  Methods for various shapes    //
    ////////////////////////////////////

    public final void boneShape(double x,
                                double y,
                                double width,
                                double height,
                                double loopWidth) {
        double right = x + width;
        double bottom = y + height;

        double xAux = x + loopWidth;
        double yAux = y + loopWidth / 2;
        moveTo(xAux, yAux);
        xAux = right - loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(right, y, right, yAux);

        xAux = right - loopWidth;
        yAux = bottom - loopWidth / 2;
        quadraticCurveTo(right, bottom, xAux, yAux);

        xAux = x + loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(x, bottom, x, yAux);

        xAux = x + loopWidth;
        yAux = y + loopWidth / 2;
        quadraticCurveTo(x, y, xAux, yAux);
        closePath();
    }

    /**
     * Draws a Bone shape rectangle - used to depict RNA nodes.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param loopWidth
     * @param needsFill
     */
    @Deprecated
    public final void drawBoneShape(double x,
                                    double y,
                                    double width,
                                    double height,
                                    double loopWidth,
                                    boolean needsFill) {
        double right = x + width;
        double bottom = y + height;

        beginPath();
        double xAux = x + loopWidth;
        double yAux = y + loopWidth / 2;
        moveTo(xAux, yAux);
        xAux = right - loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(right, y, right, yAux);

        xAux = right - loopWidth;
        yAux = bottom - loopWidth / 2;
        quadraticCurveTo(right, bottom, xAux, yAux);

        xAux = x + loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(x, bottom, x, yAux);

        xAux = x + loopWidth;
        yAux = y + loopWidth / 2;
        quadraticCurveTo(x, y, xAux, yAux);
        closePath();
        if (needsFill) {
            fill();
        }
        stroke();
    }

    public final void geneTextHolder(double x,
                                     double y,
                                     double width,
                                     double height,
                                     double symbolWidth,
                                     double arcWidth){
        double y1 = y + symbolWidth / 2;
        double right = x + width;
        double bottom = y + height;

        // Draw the horizontal line
        beginPath();
        moveTo(x, y1);
        lineTo(right, y1);
        lineTo(right, bottom - arcWidth);
        quadraticCurveTo(right, bottom, right - arcWidth, bottom);
        lineTo(x + arcWidth, bottom);
        quadraticCurveTo(x, bottom, x, bottom - arcWidth);
        closePath();
    }

    public final void geneShape(
            double x,
            double y,
            double width,
            double height,
            double symbolPad,
            double symbolWidth,
            double arrowLength,
            double arrowAngle) {
        // Draw the horizontal line
        double y1 = y + symbolWidth / 2;
        double x2 = x + width;
        double y2 = y + symbolWidth / 2;

        // Draw the horizontal line
        beginPath();
        moveTo(x, y1);
        lineTo(x2, y2);

        // Draw the vertical line
        double x1 = x2 - symbolPad;
        x2 = x1;
        y2 = (int) (y1 - symbolWidth / 2.0) + 2; // Need an extra 2 pixel. Not sure why!
        // Looks nice with one pixel offset
        moveTo(x1, y1);
        lineTo(x2, y2);
        // another very short horizontal line
        x1 += symbolPad;
        lineTo(x1, y2);
        stroke();
        // draw the arrow
        //String color = node.getLineColor();
        //if (color == null){  color = "rgba(0, 0, 0, 1)"; }
        // Keep it to reset it to the original style is always a good practice
        //FillStrokeStyle oldStyle = getFillStyle();
        //setFillStyle(CssColor.make(color));
        drawArrow(x1, y2, x1 + arrowLength, y2, arrowLength, arrowAngle, true);
        //setFillStyle(oldStyle); // Reset it back
    }

    /**
     * Draws the shape used to depict a gene.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param symbolPad
     * @param symbolWidth
     * @param arrowLength
     * @param arrowAngle
     */
    public final void drawGeneShape(double x,
                                    double y,
                                    double width,
                                    double height,
                                    double symbolPad,
                                    double symbolWidth,
                                    double arrowLength,
                                    double arrowAngle) {
        // Draw the horizontal line
        double x1 = x;
        double y1 = y + symbolWidth / 2;
        double x2 = x + width;
        double y2 = y + symbolWidth / 2;

        // Draw the horizontal line
        beginPath();
        moveTo(x1, y1);
        lineTo(x2, y2);

        // Draw the vertical line
        x1 = x2 - symbolPad;
        x2 = x1;
        y2 = (int) (y1 - symbolWidth / 2.0) + 2; // Need an extra 2 pixel. Not sure why!
        // Looks nice with one pixel offset
        moveTo(x1, y1);
        lineTo(x2, y2);
        // another very short horizontal line
        x1 += symbolPad;
        lineTo(x1, y2);
        stroke();
        // draw the arrow
        //String color = node.getLineColor();
        //if (color == null){  color = "rgba(0, 0, 0, 1)"; }
        // Keep it to reset it to the original style is always a good practice
        //FillStrokeStyle oldStyle = getFillStyle();
        //setFillStyle(CssColor.make(color));
        drawArrow(x1, y2, x1 + arrowLength, y2, arrowLength, arrowAngle, true);
        //setFillStyle(oldStyle); // Reset it back
    }

    //////////////////////////////
    //  Methods for Arrows      //
    //////////////////////////////

    /**
     * Draws an arrow from one point to another taking into account the angle of the line.
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param arrowLength
     * @param arrowAngle
     * @param needsFill
     */
    public final void drawArrow(double fromX,
                                double fromY,
                                double toX,
                                double toY,
                                double arrowLength,
                                double arrowAngle,
                                boolean needsFill) {
        // Get the angle of the line segment
        double alpha = Math.atan((double) (toY - fromY) / (toX - fromX));
        if (fromX > toX)
            alpha += Math.PI;
        double angle = arrowAngle - alpha;
        float x1 = (float) (toX - arrowLength * Math.cos(angle));
        float y1 = (float) (toY + arrowLength * Math.sin(angle));
        beginPath(); // Have to call this begin path. Otherwise, the following path will be mixed with other unknown drawings.
        moveTo(x1, y1);
        lineTo(toX, toY);
        angle = arrowAngle + alpha;
        float x2 = (float) (toX - arrowLength * Math.cos(angle));
        float y2 = (float) (toY - arrowLength * Math.sin(angle));
        lineTo(x2, y2);
        closePath();
        if (needsFill) {
//            setFillStyle(CssColor.make("rgba(255, 255, 255, 1)"));
            fill();
        }
        stroke();
    }


    //////////////////////////////
    //  Methods for Eclipses    //
    //////////////////////////////

    public final void ellipse(double x,
                              double y,
                              double width,
                              double height) {
        // Draw an ellipse
        beginPath();
        double x1 = x;
        double y1 = y + height / 2;
        moveTo(x1, y1);
        x1 = x + width;
        bezierCurveTo(x, y,
                x + width, y,
                x1, y1);
        x1 = x;
        bezierCurveTo(x + width, y + height,
                x, y + height,
                x1, y1);
        closePath();
    }

    /**
     * Draws an ellipse
     * <p/>
     * The method basically uses bezier curve with two vertexes as control points and
     * is based on this URL:
     * http://www.williammalone.com/briefs/how-to-draw-ellipse-html5-canvas/.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param needsFill
     * @param needsStroke
     */
    public final void drawEllipse(double x,
                                  double y,
                                  double width,
                                  double height,
                                  boolean needsFill,
                                  boolean needsStroke) {
        // Draw an ellipse
        beginPath();
        double x1 = x;
        double y1 = y + height / 2;
        moveTo(x1, y1);
        x1 = x + width;
        bezierCurveTo(x, y,
                x + width, y,
                x1, y1);
        x1 = x;
        bezierCurveTo(x + width, y + height,
                x, y + height,
                x1, y1);
        closePath();

        if (needsFill) {
            fill();
        }
        if (needsStroke) {
            stroke();
        }
    }

    //////////////////////////////
    //  Methods for Lines       //
    //////////////////////////////

    /**
     * Draws a dashed line from one point to another using a dashedLinePattern.
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param dashedLinePattern
     */
    @Deprecated
    public final void drawDashedLine(double fromX,
                                     double fromY,
                                     double toX,
                                     double toY,
                                     double[] dashedLinePattern) {
        beginPath();

        // Used to check if the drawing is done yet
        boolean xgreaterThan = fromX < toX;
        boolean ygreaterThan = fromY < toY;

        moveTo(fromX, fromY);

        double offsetX = fromX;
        double offsetY = fromY;
        int idx = 0;
        boolean dash = true;
        double ang = Math.atan2(toY - fromY, toX - fromX);
        double cosAng = Math.cos(ang);
        double sinAng = Math.sin(ang);

        while (!(isThereYet(xgreaterThan, offsetX, toX) && isThereYet(ygreaterThan, offsetY, toY))) {
            double len = dashedLinePattern[idx];

            offsetX = cap(xgreaterThan, toX, offsetX + (cosAng * len));
            offsetY = cap(ygreaterThan, toY, offsetY + (sinAng * len));

            if (dash)
                lineTo(offsetX, offsetY);
            else
                moveTo(offsetX, offsetY);

            idx = (idx + 1) % dashedLinePattern.length;
            dash = !dash;
        }
        closePath();
        stroke();
    }

    @Deprecated
    public final void strokeDashedLine(double fromX,
                                       double fromY,
                                       double toX,
                                       double toY,
                                       double[] dashedLinePattern) {
        if(true) return;
        beginPath();

        // Used to check if the drawing is done yet
        boolean xgreaterThan = fromX < toX;
        boolean ygreaterThan = fromY < toY;

        moveTo(fromX, fromY);

        double offsetX = fromX;
        double offsetY = fromY;
        int idx = 0;
        boolean dash = true;
        double ang = Math.atan2(toY - fromY, toX - fromX);
        double cosAng = Math.cos(ang);
        double sinAng = Math.sin(ang);
        boolean stroke = false;
        while (!(isThereYet(xgreaterThan, offsetX, toX) && isThereYet(ygreaterThan, offsetY, toY))) {
            double len = dashedLinePattern[idx];

            offsetX = cap(xgreaterThan, toX, offsetX + (cosAng * len));
            offsetY = cap(ygreaterThan, toY, offsetY + (sinAng * len));

            if (dash) {
                lineTo(offsetX, offsetY);
            }else {
                moveTo(offsetX, offsetY);
            }

            idx = (idx + 1) % dashedLinePattern.length;
            dash = !dash;

        }
        if(stroke) stroke();
    }

    // The following two methods are used to draw dashed lines, which is not supported by Context2d.
    @Deprecated
    private Boolean isThereYet(Boolean greaterThan, double a, double b) {
        if (greaterThan)
            return a >= b;
        else
            return a <= b;
    }

    @Deprecated
    private double cap(Boolean greaterThan, double a, double b) {
        if (greaterThan)
            return Math.min(a, b);
        else
            return Math.max(a, b);
    }
}
