package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.Tuple;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * The purpose of DuplicateEliminationOperator is to support DISTINCT
 * It should be noted that DuplicateEliminationOperator assumes that the results are already in order
 */
public class DuplicateEliminationOperator extends Operator {

    private PlainSelect plainSelect;

    private Operator child;

    // We use previous field to store the previous tuple
    private Tuple previous;

    public DuplicateEliminationOperator(PlainSelect plainSelect, Operator child) {
        this.plainSelect = plainSelect;
        this.child = child;
    }

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {

        // If the SQL don't have DISTINCT, DuplicateEliminationOperator is just a shell
        if (plainSelect.getDistinct() == null) {
            return this.child.getNextTuple();
        }

        if (this.previous == null) {
            this.previous = this.child.getNextTuple();
            return this.previous;
        }

        // Make comparison between previous and current tuple
        Tuple current;
        while ((current = this.child.getNextTuple()) != null) {
            if (!this.previous.equals(current)) {
                this.previous = current;
                return current;
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
        // Reset the previous as well
        this.previous = null;
    }
}
