package org.reactome.web.diagram.context.sections;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class AdvancedFlexTable extends FlexTable {

    class AdvancedCell extends Cell {
        protected AdvancedCell(int rowIndex, int cellIndex) {
            super(rowIndex, cellIndex);
        }
    }

    public Cell getCellForEvent(MouseEvent<? extends EventHandler> event) {
        Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
        if (td == null) {
            return null;
        }

        int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        int column = TableCellElement.as(td).getCellIndex();
        return new AdvancedCell(row, column);
    }
}

