package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.Tuple;

import java.io.PrintStream;

/**
 * The iterator model, every operator must extends this class
 */
public abstract class Operator {

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    public abstract Tuple getNextTuple();

    /**
     * Reset the operator
     */
    public abstract void reset();

    /**
     * Dump all result to console
     */
    public void dump() {
        PrintStream ps = new PrintStream(System.out);
        Tuple line = null;
        while ((line = this.getNextTuple()) != null){
            ps.append(line + "\n");
        }
    }

    /**
     * Dump all result to printStream
     * @param printStream can be System.out
     */
    public void dump(PrintStream printStream) {
        PrintStream ps = new PrintStream(printStream);
        Tuple line = null;
        while ((line = this.getNextTuple()) != null){
            ps.append(line + "\n");
        }
    }

}
