package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.utils.Catlog;
import ed.inf.adbs.lightdb.Tuple;
import ed.inf.adbs.lightdb.utils.Util;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

/**
 * The purpose of project operator is to support projection in Relational Algebra
 */
public class ProjectOperator extends Operator {

    private PlainSelect plainSelect;

    // The child of projectOperator could be either a scanOperator or a selectOperator
    private Operator child;

    // We need schema information to handle projection
    private List<String> schema;

    public ProjectOperator(PlainSelect plainSelect, Operator child) {
        this.plainSelect = plainSelect;
        this.child = child;
        // Obtain the schema in the order of declaration of the SQL statement table
        this.schema = Catlog.getInstance().getSchemas(plainSelect);
    }


    /**
     * Get the next tuple of this operator
     *
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {
        Tuple tuple = this.child.getNextTuple();
        if (tuple == null) {
            return null;
        }

        // In the case of select *, do not need to do any processing, just return directly
        if (this.plainSelect.getSelectItems().get(0).toString().equals("*")) {
            return tuple;
        }
        // Select the given column from the tuple
        StringBuilder sb = new StringBuilder();
        for (SelectItem selectItem : this.plainSelect.getSelectItems()) {
            Column column = (Column) ((SelectExpressionItem) selectItem).getExpression();
            // For the use of aliases, special treatment is required
            if (Util.useAliases(this.plainSelect)) {
                // In this case, e.g., schema=[R.G, R.H], column.toString()=R.G
                sb.append(tuple.get(this.schema.indexOf(column.toString())));
            } else {
                // In this case, e.g., schema=[G, H], column.getColumnName()=G
                sb.append(tuple.get(this.schema.indexOf(column.getColumnName())));
            }
            sb.append(",");
        }
        // Remove the last comma, e.g., 1,2,3, -> 1,2,3
        tuple = new Tuple(sb.substring(0, sb.length() - 1));
        return tuple;
    }


    /**
     * Reset the operator
     */
    @Override
    public void reset() {
        this.child.reset();
    }
}
