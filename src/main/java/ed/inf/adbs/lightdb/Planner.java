package ed.inf.adbs.lightdb;

import ed.inf.adbs.lightdb.operator.*;
import ed.inf.adbs.lightdb.parser.JoinExpressionDeParser;
import ed.inf.adbs.lightdb.utils.Catlog;
import ed.inf.adbs.lightdb.utils.Util;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

/**
 * The purpose of this class is to create a query plan.
 */
public class Planner {

    /**
     * This method is used to create a query plan
     * @param plainSelect SQL plainSelect
     * @return An operator, i.e., query plan
     */
    public static Operator constructQueryPlan(PlainSelect plainSelect) {
        Operator queryPlan = null;

//        System.out.println(plainSelect);

        // Determine whether join is needed
        Boolean needJoin = plainSelect.getJoins() != null ? true : false;

        // No need to join
        if (!needJoin) {
            queryPlan = new SelectOperator(plainSelect.getWhere(), plainSelect.getFromItem().toString());
        }

        // Need join
        if (needJoin) {
            // Analyze expression, separate selection and condition to obtain selection condition list and join condition list
            JoinExpressionDeParser joinExpressionDeParser = new JoinExpressionDeParser();
            Expression where = plainSelect.getWhere();
            // In the case of join, WHERE can also be null, e.g., SELECT * FROM Sailors S, Sailors R
            if (where != null) {
                where.accept(joinExpressionDeParser);
            }

            List<String> tableNameList = Catlog.getTableNameList(plainSelect);
            Map<String, Operator> selectionMap = new HashMap<>();  // [tableName, operator]


            // The selection condition is processed first, process the selection of the corresponding table in the order of From
            // It should be noted that a selection may have multiple conditions, if there is no condition,
            // create a select operator with null expression (In this case, the selectOperator is just a shell).
            // And, if there is a selection expression such as 1=1, the corresponding tableName is the first tableName in the from clause
            for (String s : tableNameList) { // Use AND to splice multiple selections corresponding to the same tableName
                List<Expression> conditions = new ArrayList<>();
                for (Expression expression : joinExpressionDeParser.getSelectionConditionList()) {
                    List<String> l = Util.getTableName(expression);
                    // l.size may be equal to 0, the corresponding situation is that the expression is 1=1, that is, the left and right sides are both numbers.
                    if (l.size() == 0) {
                        // When both sides are numbers, the corresponding table should be the first table in the from clause
                        if (s.equals(plainSelect.getFromItem().toString())) {
                            conditions.add(expression);
                        }
                    } else {
                        // Deal with the possible use of Alias
                        String[] ss = s.split(" ");
                        if (ss[ss.length - 1].equals(l.get(0))) {
                            conditions.add(expression);
                        }
                    }
                }
                Expression expression = Util.mergeExpressionWithAnd(conditions);
                // The expression passed in may be null. When it is null, it means that there is no selection condition.
                SelectOperator selectOperator = new SelectOperator(expression, s);
                // Store the selectOperator in a map, s is tableName.
                selectionMap.put(s, selectOperator);
            }

            JoinOperator previousOperator = null;
            List<String> previousSchema = null;
            // Then process the join condition
            // Process the join between the two tables in the order of From. It should be noted that a join may have multiple conditions
            for (int i = 0; i < tableNameList.size() - 1; i++) {

                Operator leftChild;
                Operator rightChild;

                List<String> left = tableNameList.subList(0, i + 1);
                String right = tableNameList.get(i + 1);

                // Get schema
                List<String> schema = new ArrayList<>();
                List<String> leftSchema = previousSchema == null ? Catlog.getInstance().getSchemaByTableName(left.get(left.size() - 1)) : previousSchema;
                List<String> rightSchema = Catlog.getInstance().getSchemaByTableName(right);

                // Determine whether an alias is used. If an alias is used, special processing is required for the schema
                if (Util.useAliases(plainSelect)) {
                    Util.addAlias2Schema(leftSchema, left.get(left.size() - 1).split(" ")[1]);
                    Util.addAlias2Schema(rightSchema, right.split(" ")[1]);
                }
                schema.addAll(leftSchema);
                schema.addAll(rightSchema);

                // Get the corresponding expression
                Expression expression = Util.getEligibleExpression(left, right, tableNameList, joinExpressionDeParser.getJoinConditionList());

                // leftChild will be taken from selectionMap for the first time, and then taken from previousOperator
                leftChild = previousOperator == null ? selectionMap.get(left.get(left.size() - 1)) : previousOperator;
                rightChild = selectionMap.get(right);

                // The expression passed in may be null, if it is null, it means that there is no join condition, that is, cross product
                previousOperator = new JoinOperator(expression, schema, leftChild, rightChild);
                previousSchema = schema;
            }
            queryPlan = previousOperator;
        }
        ProjectOperator projectOperator = new ProjectOperator(plainSelect, queryPlan);
        SortOperator sortOperator = new SortOperator(plainSelect, projectOperator);

        // If DISTINCT exists, ORDER BY not exists, we need to manually add the ORDER BY statement
        if (plainSelect.getDistinct() != null) {
            sortOperator = new SortOperator(Util.addOrderBy(plainSelect), projectOperator);
        }


        queryPlan = new DuplicateEliminationOperator(plainSelect, sortOperator);

        return queryPlan;
    }

}
