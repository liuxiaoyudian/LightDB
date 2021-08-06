package ed.inf.adbs.lightdb.parser;

import ed.inf.adbs.lightdb.Tuple;
import ed.inf.adbs.lightdb.utils.Util;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.util.*;

/**
 * Visitor class
 * This class takes a tuple as input and traverses the expression recursively to evaluate the tuple as true or false.
 */
public class SelectExpressionDeParser extends ExpressionDeParser {

    private Expression expression;

    private List<String> schema;

    private Tuple tuple;

    private List<Boolean> list = new ArrayList<>();  // Use list to store all the result

    public SelectExpressionDeParser(Expression expression, List<String> schema) {
        this.expression = expression;
        this.schema = schema;
    }

    /**
     * Because where clause is a conjunction (AND) of expressions of the form of A op B,
     * so, when all A op B are true, the expression is equal to true.
     * @param tuple
     * @return True if the tuple satisfies the evaluation condition
     */
    public Boolean evaluate(Tuple tuple) {
        this.tuple = tuple;
        this.list.clear();
        // this.expression是被访问的类，this是访问者类
        this.expression.accept(this);
        this.tuple = null;
        return !list.contains(false);
    }

    /**
     * Determine which operator to use
     * @param left int
     * @param right int
     * @param operator Could be =,!=,>,>=,<,<=
     */
    public void comparison(Integer left, Integer right, String operator) {
        switch (operator) {
            case "=":
                list.add(left.equals(right));
                break;
            case "!=":
                list.add(!left.equals(right));
                break;
            case "<":
                list.add(left < right);
                break;
            case "<=":
                list.add(left <= right);
                break;
            case ">":
                list.add(left > right);
                break;
            case ">=":
                list.add(left >= right);
                break;
            default:
                System.err.println("Operator " + operator + " is not supported");
        }
    }

    /**
     * Every visit method will call this method
     * @param expression e.g, R.H=102
     * @param operator Could be =,!=,>,>=,<,<=
     */
    private void commonMethod(ComparisonOperator expression, String operator) {
        Expression leftExpression = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();

        Boolean isLeftDigit = Util.isInteger(leftExpression.toString());
        Boolean isRightDigit = Util.isInteger(rightExpression.toString());

        // Determine whether Alias is used. If Alias is used, the schema is like this [S.A, S.B, S.C],
        // and if it is not used, it is like this [A, B, C]
        Boolean useAliases = this.schema.get(0).split("\\.").length == 2;

        // The left and right sides of the equation are numbers
        if (isLeftDigit && isRightDigit) {
            Integer left = Integer.valueOf(leftExpression.toString());
            Integer right = Integer.valueOf(rightExpression.toString());
            comparison(left, right, operator);
        }

        // The left side of the equation is not a number, the right side is a number
        if (!isLeftDigit && isRightDigit) {
            Column column = (Column) leftExpression;
            // Adjust how to get index according to useAliases
            Integer index = useAliases ? this.schema.indexOf(column.toString()) : this.schema.indexOf(column.getColumnName());
            Integer left = tuple.get(index);
            Integer right = Integer.valueOf(rightExpression.toString());
            comparison(left, right, operator);
        }

        // The left side of the equation is a number, the right side is not a number
        if (isLeftDigit && !isRightDigit) {
            Column column = (Column) rightExpression;
            // Adjust how to get index according to useAliases
            Integer index = useAliases ? this.schema.indexOf(column.toString()) : this.schema.indexOf(column.getColumnName());
            Integer left = Integer.valueOf(leftExpression.toString());
            Integer right = tuple.get(index);
            comparison(left, right, operator);
        }

        // In this case, there are two possibilities: selection condition and join condition, depending on how many schemas there are
        if (!isLeftDigit && !isRightDigit) {
            Column leftColumn = (Column) leftExpression;
            Column rightColumn = (Column) rightExpression;
            // Adjust how to get leftIndex and rightIndex according to useAliases
            Integer leftIndex = useAliases ? this.schema.indexOf(leftColumn.toString()) : this.schema.indexOf(leftColumn.getColumnName());
            Integer rightIndex = useAliases ? this.schema.indexOf(rightColumn.toString()) : this.schema.indexOf(rightColumn.getColumnName());
            Integer left = tuple.get(leftIndex);
            Integer right = tuple.get(rightIndex);
            comparison(left, right, operator);
        }
    }


    @Override
    public void visit(EqualsTo equalsTo) {
        super.visit(equalsTo);
        commonMethod(equalsTo, "=");
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        super.visit(notEqualsTo);
        commonMethod(notEqualsTo, "!=");
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        super.visit(greaterThan);
        commonMethod(greaterThan, ">");
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        super.visit(greaterThanEquals);
        commonMethod(greaterThanEquals, ">=");
    }

    @Override
    public void visit(MinorThan minorThan) {
        super.visit(minorThan);
        commonMethod(minorThan, "<");
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        super.visit(minorThanEquals);
        commonMethod(minorThanEquals, "<=");
    }

}

