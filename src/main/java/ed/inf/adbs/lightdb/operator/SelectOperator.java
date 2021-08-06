package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.parser.SelectExpressionDeParser;
import ed.inf.adbs.lightdb.Tuple;
import ed.inf.adbs.lightdb.utils.Catlog;
import ed.inf.adbs.lightdb.utils.Util;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * Select Operator is used to support single-table selection.
 */
public class SelectOperator extends Operator {

    // The child of select operator is scan operator
    private Operator child;

    private SelectExpressionDeParser selectExpressionDeParser;


    /**
     * The expression of SelectOperator will have 0 or 1 table.
     * @param expression
     * @param tableName
     */
    public SelectOperator(Expression expression, String tableName) {
        this.child = new ScanOperator(tableName);
        // When expression == null, it means that there is no corresponding selection condition.
        // In this case, SelectOperator is just a shell, and ScanOperation really plays a role
        if (expression != null) {
            List<String> schema = Catlog.getInstance().getSchemaByTableName(tableName);
            // Determine whether Alias is used according to the table name (Sailors S)
            if (tableName.split(" ").length == 2) {
                // If alias exists, we need to process the schema
                Util.addAlias2Schema(schema, tableName.split(" ")[1]);
            }
            this.selectExpressionDeParser = new SelectExpressionDeParser(expression, schema);
        }
    }

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {
        Tuple tuple;

        while ((tuple = this.child.getNextTuple()) != null) {
            if (this.selectExpressionDeParser == null) {
                return tuple;
            }
            // Use SelectExpressionDeParser to evaluate the tuple
            if (this.selectExpressionDeParser.evaluate(tuple)) {
                return tuple;
            }
        }
        return null;
    }

    /**
     * Reset the operator
     */
    @Override
    public void reset() {
        this.child.reset();
    }
}
