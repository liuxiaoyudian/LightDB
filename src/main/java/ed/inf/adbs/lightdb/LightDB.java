package ed.inf.adbs.lightdb;

import ed.inf.adbs.lightdb.operator.*;
import ed.inf.adbs.lightdb.utils.Catlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;


/**
 * Lightweight in-memory database system
 */
public class LightDB {

    public static void main(String[] args) {
//        boolean debug = true; // debug mode

        String currentPath = System.getProperty("user.dir");
        String databaseDir = args.length >= 3 ? currentPath + args[0] : currentPath + "/samples/db";
        String inputFile = args.length >= 3 ? currentPath + args[1] : currentPath + "/samples/input/query4.sql";
        String outputFile = args.length >= 3 ? currentPath + args[2] : currentPath + "/samples/output/query4.csv";
        boolean debug = args.length >= 4 ? false : true;

        // Set the path of database
        Catlog.getInstance().setDatabaseDir(databaseDir);

        Interpreter interpreter = new Interpreter(inputFile);
        Operator queryPlan = interpreter.getQueryPlan();

        if (debug) {
            queryPlan.dump();
        } else {
            try {
                File file = new File(outputFile.substring(0, outputFile.lastIndexOf("/")));
                if (!file.exists()) { // If output file is not exists, create an output file
                    file.mkdir();
                }
                queryPlan.dump(new PrintStream(outputFile)); // Dump the result of the given SQL statement
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
