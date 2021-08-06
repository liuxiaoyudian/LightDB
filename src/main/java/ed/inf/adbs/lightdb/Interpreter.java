package ed.inf.adbs.lightdb;


import ed.inf.adbs.lightdb.operator.Operator;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.io.FileReader;

/**
 * The purpose of this class is to read statement from the query file
 */
public class Interpreter {

    // query plan
    Operator root;

    /**
     * In the process of instantiating the Interpreter object, a query plan is created
     *
     * @param inputFile the path of the given SQL file
     */
    public Interpreter(String inputFile) {
        try {
            Statement statement = CCJSqlParserUtil.parse(new FileReader(inputFile));
//            Statement statement = CCJSqlParserUtil.parse("SELECT * FROM Reserves WHERE Reserves.H >= 101 AND Reserves.H <= 103;");
            Select select = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            this.root = Planner.constructQueryPlan(plainSelect); // Use the Planner class to create a query plan
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

    /**
     * return a query plan of the given SQL statement
     *
     * @return Operator
     */
    public Operator getQueryPlan() {
        return this.root;
    }

}
