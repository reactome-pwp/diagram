/*
 * Copyright 2011 Hao Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.reactome.web.diagram.renderers.impl.abs;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Helper class for drawing dashed line.
 * Reference: http://davidowens.wordpress.com/2010/09/07/html-5-canvas-and-dashed-lines/
 *
 * @author hao1300@gmail.com
 */
public abstract class DashedLineAbstractRenderer {

    /**
     * Draw a dashed line from (fromX, fromY) to (toX, toY).
     *
     * @param context
     * @param fromX x-coordinate of the starting point
     * @param fromY y-coordinate of the starting point
     * @param toX x-coordinate of the ending point
     * @param toY y-coordinate of the ending point
     * @param dashLength length of the dash
     * @param gapLength length of the gap in between dashes
     */
    public static void drawDashedLine(Context2d context, double fromX, double fromY,
                                      double toX, double toY, double dashLength, double gapLength) {
        DashedLineHelper checkX = GreaterThanHelper.instance;
        DashedLineHelper checkY = GreaterThanHelper.instance;

        if (fromY - toY > 0) {
            checkY = LessThanHelper.instance;
        }
        if (fromX - toX > 0) {
            checkX = LessThanHelper.instance;
        }

        context.moveTo(fromX, fromY);
        double offsetX = fromX;
        double offsetY = fromY;
        boolean dash = true;
        double ang = Math.atan2(toY - fromY, toX - fromX);
        while (!(checkX.isThereYet(offsetX, toX) && checkY.isThereYet(offsetY, toY))) {
            double len = (dash) ? dashLength : gapLength;

            offsetX = checkX.getCap(toX, offsetX + (Math.cos(ang) * len));
            offsetY = checkY.getCap(toY, offsetY + (Math.sin(ang) * len));

            if (dash) {
                context.lineTo(offsetX, offsetY);
            } else {
                context.moveTo(offsetX, offsetY);
            }
            dash = !dash;
        }
    }

    /**
     * Helper class for checking the dash line.
     */
    private interface DashedLineHelper {

        /**
         * Checks whether the point has been reached yet.
         *
         * @param from
         * @param to
         * @return
         */
        boolean isThereYet(double from, double to);

        /**
         * Gets the cap of the two values. If the line is increasing, this will
         * return max(v1, v2). If the line is decreasing, this will return
         * min(v1, v2).
         *
         * @param v1
         * @param v2
         * @return
         */
        double getCap(double v1, double v2);
    }

    /**
     * Helper for a decreasing line.
     */
    private static class LessThanHelper implements DashedLineHelper {
        private static DashedLineHelper instance = new LessThanHelper();

        @Override
        public double getCap(double v1, double v2) {
            return Math.max(v1, v2);
        }

        @Override
        public boolean isThereYet(double from, double to) {
            return from <= to;
        }
    }

    /**
     * Helper for an increasing line.
     */
    private static class GreaterThanHelper implements DashedLineHelper {
        private static DashedLineHelper instance = new GreaterThanHelper();

        @Override
        public double getCap(double v1, double v2) {
            return Math.min(v1, v2);
        }

        @Override
        public boolean isThereYet(double from, double to) {
            return from >= to;
        }
    }
}

