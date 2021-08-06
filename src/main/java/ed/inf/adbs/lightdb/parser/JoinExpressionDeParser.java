package ed.inf.adbs.lightdb.parser;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to pare the SQL statement and extract the join and selection condition to two different list
 */
public class JoinExpressionDeParser extends ExpressionDeParser {

    // Use joinConditionList to store join conditions
    private List<Expression> joinConditionList = new ArrayList<>();

    // Use selectionConditionList to store selection conditions
    private List<Expression> selectionConditionList = new ArrayList<>();

    /**
     * The conditions for judging whether it is a join are：
     * 1、Neither left nor right can be numbers
     * 2、The table names on the left and right are different
     * For example：
     * left = Reserves.G, right = Sailors.A, return true
     * left = Reserves.H, right = 102, return false
     * @param left string
     * @param right string
     * @return return ture if is join, otherwise return false
     */
    public static Boolean isJoin(String left, String right) {
        Boolean isJoin = false;
        // Determine whether left and right are numbers
        if (!left.matches("\\d+") && !right.matches("\\d+")) {
            isJoin =  !left.split("\\.")[0].equals(right.split("\\.")[0]);
        }
        return isJoin;
    }

    /**
     * Extract the join, that is, parse the where clause, get the join condition
     * and the selection condition respectively, and then put these two conditions in the corresponding list and store.
     * @param expression
     */
    private void extractJoin(ComparisonOperator expression) {
        String left = expression.getLeftExpression().toString();
        String right = expression.getRightExpression().toString();
        // Determine whether this expression is belong to join or selection condition
        if (isJoin(left, right)) {
            this.joinConditionList.add(expression);
        } else {
            this.selectionConditionList.add(expression);
        }
    }


    /**
     * Get joinConditionList
     * @return joinConditionList
     */
    public List<Expression> getJoinConditionList() {
        return joinConditionList;
    }

    /**
     * Get selectionConditionList
     * @return selectionConditionList
     */
    public List<Expression> getSelectionConditionList() {
        return selectionConditionList;
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        super.visit(equalsTo);
        extractJoin(equalsTo);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        super.visit(notEqualsTo);
        extractJoin(notEqualsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        super.visit(greaterThan);
        extractJoin(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        super.visit(greaterThanEquals);
        extractJoin(greaterThanEquals);
    }

    @Override
    public void visit(MinorThan minorThan) {
        super.visit(minorThan);
        extractJoin(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        super.visit(minorThanEquals);
        extractJoin(minorThanEquals);
    }
}
