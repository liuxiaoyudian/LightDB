package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.Tuple;
import ed.inf.adbs.lightdb.parser.SelectExpressionDeParser;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * Join operator is used to support join condition in Relational Algebra
 */
public class JoinOperator extends Operator {

    // Join operator have two child
    private Operator leftChild;

    private Operator rightChild;

    // Mark the outer loop, one outer loop may match multiple inner loops
    private Tuple leftMark = null;

    private SelectExpressionDeParser selectExpressionDeParser;

    /**
     * When expression == null, it means that it is a cross product, otherwise it is the case of condition join
     * @param expression
     * @param schema
     * @param leftChild operator
     * @param rightChild operator
     */
    public JoinOperator(Expression expression, List<String> schema, Operator leftChild, Operator rightChild) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        if (expression != null) {
            this.selectExpressionDeParser = new SelectExpressionDeParser(expression, schema);
        }
    }

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {
        Tuple left;
        // if leftMark != null, use leftMark as left directly
        while ((left = this.leftMark == null ? this.leftChild.getNextTuple() : this.leftMark) != null) {
            Tuple right;
            while ((right = this.rightChild.getNextTuple()) != null) {
                if (this.selectExpressionDeParser != null) {
                    // We use SelectExpressionDeParser to evaluate the tuple
                    if (this.selectExpressionDeParser.evaluate(Tuple.add(left, right))) {
                        this.leftMark = left;
                        return Tuple.add(left, right);
                    }
                } else { // cross product, do not need to evaluate
                    this.leftMark = left;
                    return Tuple.add(left, right);
                }
            }
            this.rightChild.reset();
            this.leftMark = null;
        }
        return null;
    }

    /**
     * Reset the operator
     */
    @Override
    public void reset() {
        this.leftChild.reset();
    }
}
