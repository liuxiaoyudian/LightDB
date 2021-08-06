package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.Tuple;
import ed.inf.adbs.lightdb.utils.Catlog;
import ed.inf.adbs.lightdb.utils.Util;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The purpose of SortOperator is to support ORDER BY.
 */
public class SortOperator extends Operator {

    private PlainSelect plainSelect;

    private Operator child;

    // Use buffer to store all tuple result
    private List<Tuple> buffer = new ArrayList<>();

    // Use pointer to indicate the position of the next tuple
    private Integer pointer = 0;

    public SortOperator(PlainSelect plainSelect, Operator child) {
        this.plainSelect = plainSelect;
        this.child = child;
    }

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {
        // If the SQL statement do not have ORDER BY statement, SortOperator is just a shell
        if (this.plainSelect.getOrderByElements() == null) {
            return this.child.getNextTuple();
        }

        // When the getNextTuple is called for the first time, the sort operator will get all the tuples
        // from the child operator, then sort these tuples in the given order, and finally store them in the buffer.
        if (this.buffer.size() == 0) {
            Boolean useAliases = Util.useAliases(this.plainSelect);

            // e.g., [*], [R.G, R.H], [Reserves.G, Reserves.H]
            List<SelectItem> selectItemList = this.plainSelect.getSelectItems();
            List<String> selectItems = new ArrayList<>();

            // Determine whether it is a SELECT *, if it is, we will manually modify selectItems,
            // and change * to [A, B, C] or [S.A, S.B, S.C]
            if (selectItemList.get(0).toString().equals("*")) { // When SELECT *
                List<String> schemas = Catlog.getInstance().getSchemas(this.plainSelect);
                if (useAliases) {
                    // In this case, e.g., schemas = [R.G, R.H]
                    selectItems = schemas;
                }
                else {
//                     In this case, e.g., schemas = [G, H]
                    for (String s : schemas) {
                        selectItems.add(s);
                    }
                }
            } else { // When SELECT R.G, R.H
                // When it comes to SortOperator, it has already experienced projection, so the tuple has been changed
                // and cannot be calculated according to the original schema.
                // The structure of the tuple is the same as the select statement.
                for (SelectItem selectItem : selectItemList) {
                    if (useAliases) {
                        selectItems.add(selectItem.toString());
                    } else {
                        selectItems.add(selectItem.toString().split("\\.")[1]);
                    }
                }
            }


            // Obtain the index corresponding to the column name in the order of the order by declaration
            List<Integer> order = new ArrayList<>();
            for (OrderByElement orderByElement : this.plainSelect.getOrderByElements()) {
                Column column = (Column) orderByElement.getExpression();
                // true: selectItems=[R.G,R.H], column.toString()=[R.G]
                // false: selectItems=[G,H], column.getColumnName()=[G]
                Integer index = useAliases ? selectItems.indexOf(column.toString()) : selectItems.indexOf(column.getColumnName());
                order.add(index);
            }

            // Get all tuple from the child operator
            Tuple tuple;
            while ((tuple = this.child.getNextTuple()) != null) {
                buffer.add(tuple);
            }

            // Sort all tuple internally
            Collections.sort(this.buffer, new Comparator<Tuple>() {
                @Override
                public int compare(Tuple o1, Tuple o2) {
                    int temp = 0;
                    for (Integer index : order) {
                        if ((temp = o1.get(index) - o2.get(index)) != 0) {
                            return temp;
                        }
                    }
                    return temp;
                }
            });
        }
        // Make sure pointer is always less than buffer size
        return this.pointer < this.buffer.size() ? this.buffer.get(this.pointer++) : null;
    }

    /**
     * Reset the operator
     */
    @Override
    public void reset() {
        this.child.reset();
        // Reset the pointer as well
        this.pointer = 0;
    }
}
