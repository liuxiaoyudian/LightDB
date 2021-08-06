package ed.inf.adbs.lightdb.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class, frequently used methods will be extracted to this class
 */
public class Util {

    /**
     * Add an order by statement to plainSelect
     * e.g., SELECT DISTINCT R.G FROM Reserves -> SELECT DISTINCT R.G FROM Reserves R ORDER BY R.G
     * @param plainSelect
     * @return PlainSelect with ORDER BY
     */
    public static PlainSelect addOrderBy(PlainSelect plainSelect) {
        PlainSelect res = null;
        StringBuilder orderBy = new StringBuilder();
        // Determine whether it is select *
        if (plainSelect.getSelectItems().get(0).toString().contains("*")) {
            for (String s : Catlog.getInstance().getSchemas(plainSelect)) {
                orderBy.append(s + ",");
            }
        } else {
            for (SelectItem selectItem : plainSelect.getSelectItems()) {
                orderBy.append(selectItem.toString() + ",");
            }
        }
        // Add ORDER BY to the original SQL statement
        String sql;
        if (plainSelect.getOrderByElements() == null) {
            sql = plainSelect.toString() + " ORDER BY " + orderBy.substring(0, orderBy.length() - 1);
        } else { // ORDER BY clause already exists
            String ss = "";
            for (String s : orderBy.toString().split(",")) {
                Boolean flag = true;
                for (OrderByElement orderByElement : plainSelect.getOrderByElements()) {
                    if (s.equals(orderByElement.toString())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    ss += s + ",";
                }
            }
            sql = plainSelect.toString() + "," + ss.substring(0, ss.length() - 1);
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            Select select = (Select) statement;
            res = (PlainSelect) select.getSelectBody();
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Add Alias to the schema
     * e.g., schema = [A, B, C], alias = S -> [S.A, S.B, S.C]
     * @param schema
     * @param alias
     */
    public static void addAlias2Schema(List<String> schema, String alias) {
        // Avoid adding alias repeatedly, if the schema is already similar to [R.G, R.H], nothing will be done
        if (!schema.get(0).contains(".")) {
            for (int i = 0; i < schema.size(); i++) {
                schema.set(i, alias + "." + schema.get(i));
            }
        }
    }

    /**
     * Determine whether an alias is used
     * @param plainSelect
     * @return true if Aliases is used, otherwise return false
     */
    public static Boolean useAliases(PlainSelect plainSelect) {
        return plainSelect.getFromItem().getAlias() == null ? false : true;
    }

    /**
     * Merge two expression with and
     * e.g., we have two expressions: Reserves.H >= 101 and Reserves.H <= 103
     * After the merge, the result will be: Reserves.H >= 101 AND Reserves.H <= 103
     * @param conditions
     * @return Merged expression
     */
    public static Expression mergeExpressionWithAnd(List<Expression> conditions) {
        Expression expression = null;
        // conditions.size == 1 means that this table has only 1 selection condition
        if (conditions.size() == 1) {
            expression = conditions.get(0);
        }

        // conditions.size> 1 means that this table has multiple selection conditions
        if (conditions.size() > 1) {
            for (int i = 0; i < conditions.size() - 1; i++) {
                Expression a = i == 0 ? conditions.get(i) : expression;
                Expression b = conditions.get(i + 1);
                expression = new AndExpression(a, b);
            }
        }
        return expression;
    }


    /**
     * Parse the expression, and return the tableName appearing in the expression in order
     * For example,
     * Reserves.H >= 101, return Reserves
     * Reserves.H >= 101 AND Reserves.H <= 103, return Reserves
     * Reserves.G = Sailors.A, return Reserves and Sailors
     *
     * @param expression
     * @return table name as a string list
     */
    public static List<String> getTableName(Expression expression) {
        List<String> res = new ArrayList<>();
        // Use AND to split expression
        String[] l = expression.toString().split("AND");

        for (String s : l) {
            // Extract both sides of the equation
            String left = s.split("= | != | > | >= | < | <=")[0].trim();
            String right = s.split("= | != | > | >= | < | <=")[1].trim();

            if (!isInteger(left)) {
                String tableName = left.split("\\.")[0];
                if (!res.contains(tableName)) {
                    res.add(left.split("\\.")[0]);
                }
            }

            if (!isInteger(right)) {
                String tableName = right.split("\\.")[0];
                if (!res.contains(tableName)) {
                    res.add(right.split("\\.")[0]);
                }
            }
        }
        return res;
    }


    /**
     * Obtain the corresponding conditions according to left and right, and then join these conditions together using AND
     * @param left
     * @param right
     * @param tableNameList
     * @param joinConditionList
     * @return
     */
    public static Expression getEligibleExpression(List<String> left, String right, List<String> tableNameList, List<Expression> joinConditionList) {
        // Store qualified expression
        List<Expression> conditions = new ArrayList<>();
        // The join condition may have multiple conditions. Put multiple conditions together to get a List<Expression> conditions
        for (Expression expression : joinConditionList) {
            List<String> list = Util.getSortedTableNameFromExpression(expression, tableNameList);
            // Join may have many conditions
            for (String s : left) {
                String[] l = s.split(" ");
                String[] r = right.split(" ");
                if (l[l.length - 1].equals(list.get(0)) && r[r.length - 1].equals(list.get(1))) {
                    conditions.add(expression);
                    break;
                }
            }
        }
        // If the size of the conditions is equal to 0, the expression will be null,
        // that is, in the case of a cross product, there is no condition
        // If the size of the conditions is greater than 0, these conditions will be combined into one andExpression
        Expression expression = mergeExpressionWithAnd(conditions);
        return expression;
    }

    /**
     * Obtain tableName from expression, and arrange these tableNames in the order of tableName in the from clause
     * For example:
     * SELECT * FROM Sailors S, Reserves R WHERE R.G = S.A AND R.H = 102
     * expression = R.G = S.A
     * return [S, R]
     *
     * @param expression
     * @param tableNameList
     * @return sorted table name as a string list
     */
    public static List<String> getSortedTableNameFromExpression(Expression expression, List<String> tableNameList) {
        List<String> res = Util.getTableName(expression);

        List<String> l = new ArrayList<>(tableNameList);
        for (int i = 0; i < tableNameList.size(); i++) {
            String[] split = tableNameList.get(i).split(" ");
            l.set(i, split[split.length - 1]);
        }

        // Adjust the position of the two tables in the expression to ensure the same order as in from
        if (l.indexOf(res.get(0)) > l.indexOf(res.get(1))) {
            String temp = res.get(0);
            res.set(0, res.get(1));
            res.set(1, temp);
        }
        return res;
    }

    /**
     * Determine whether it is a number
     * @param s string
     * @return true if it is a number, otherwise return false
     */
    public static Boolean isInteger(String s) {
        return s.matches("\\d+");
    }


}
