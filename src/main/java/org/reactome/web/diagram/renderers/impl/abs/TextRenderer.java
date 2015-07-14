package org.reactome.web.diagram.renderers.impl.abs;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeCommon;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class TextRenderer {
    private double fontSize;
    private double padding;

    public TextRenderer(double fontSize, double padding) {
        this.fontSize = fontSize;
        this.padding = padding;
    }

    public TextRenderer(double fontSize) {
        this.fontSize = fontSize;
        this.padding = 0;
    }

    protected void drawTextSingleLine(AdvancedContext2d ctx, String message, Coordinate centerPosition, Double factor, Coordinate offset){
        Coordinate newCenter = CoordinateFactory.get(centerPosition.getX(), centerPosition.getY()).transform(factor,offset);
        drawTextSingleLine(ctx, message, newCenter);
    }

    protected void drawTextSingleLine(AdvancedContext2d ctx, String message, Coordinate centerPosition){
        ctx.fillText(message, centerPosition.getX(), centerPosition.getY());
    }

    protected void drawTextMultiLine(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset){
        NodeProperties prop = NodePropertiesFactory.transform( ((NodeCommon)item).getProp(), factor, offset);

        double availableWidth = prop.getWidth() - padding;

        List<String> textLines = spitText(ctx, item.getDisplayName(), availableWidth);

        double x = prop.getX() + prop.getWidth() / 2;
        double totalHeight = textLines.size() * fontSize;
        double y = (prop.getHeight() - totalHeight) / 2 + prop.getY();

        for (int i=0; i<textLines.size(); i++) {
            ctx.fillText(
                    textLines.get(i),
                    x,
                    y + (i * fontSize) +  fontSize/2
            );
        }
    }

    //*********** Methods used to split the long text  *************//
    private static List<String> spitText(AdvancedContext2d ctx, String fullName, double availableWidth){
        List<String> rtn = new LinkedList<String>();

        //1. split all words
        String[] words = fullName.trim().split(" ");
        if(words.length>0) {
            StringBuilder singleLine = new StringBuilder();
            for(String word : words) {
                // check if word is too large
                if(ctx.measureText(word).getWidth() > availableWidth){
                    splitLongWord(ctx, rtn, singleLine, word, availableWidth);
                    continue;
                }
                if (ctx.measureText(singleLine.toString()).getWidth() + ctx.measureText(word).getWidth() > availableWidth) {
                    rtn.add(singleLine.toString());
                    singleLine.setLength(0);
                }
                singleLine.append(word).append(" ");
            }
            rtn.add(singleLine.toString());
        }
        return rtn;
    }

    private static void splitLongWord(AdvancedContext2d ctx, List<String> allLines, StringBuilder currentLine, String longWord, double availableWidth){
        double currentLineWidth = ctx.measureText(currentLine.toString()).getWidth();

        //Attempt to split the word backwards
        for (int i = longWord.length() - 1; i >= 0; i--) {
            char letter = longWord.charAt(i);
            // The word can be spitted in the following points
            if(letter == ':' || letter == '.' || letter == '-' || letter == ',' || letter == ')' || letter == '/'){
                double extraWidth = ctx.measureText(longWord.substring(0, i)).getWidth();
                if (currentLineWidth + extraWidth <= availableWidth) {
                    // Split at this position
                    String firstPart = longWord.substring(0, i+1);
                    String secondPart = longWord.substring(i+1);

                    // Add first part into the currentLine
                    currentLine.append(firstPart);

                    // Add curentLine to the list of lines
                    allLines.add(currentLine.toString());

                    // Reset currentLine
                    currentLine.setLength(0);

                    // If word needs further splitting call the method again
                    if(ctx.measureText(secondPart).getWidth()>availableWidth) {
                        splitLongWord(ctx, allLines, currentLine, secondPart, availableWidth);
                    }else{
                        // Add the second part of the split word into the new line
                        currentLine.append(secondPart).append(" ");
                    }
                    break;
                }
            }
            if(i==0 && currentLine.length()>0){
                //change line and try to split again
                allLines.add(currentLine.toString());
                currentLine.setLength(0);
                splitLongWord(ctx, allLines, currentLine, longWord, availableWidth);
            }
        }
    }
}
