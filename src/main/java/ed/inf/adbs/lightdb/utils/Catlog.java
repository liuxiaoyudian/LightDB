package ed.inf.adbs.lightdb.utils;

import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Database catlog, used to keep track of relevant information
 */
public class Catlog {

    // Use singleton pattern
    private static Catlog catlog = new Catlog();

    private String databaseDir = null;

    private Map<String, List<String>> schemaMap = new HashMap<>();  // {tableName, schema}

    private Catlog() {
    }

    /**
     * There are two types of supported input:
     * 1、Sailors S
     * 2、Sailor
     * The results of both types input returned are all the same
     *
     * @param tableName
     * @return The table path
     */
    public String getFileByTableName(String tableName) {
        if (this.databaseDir != null) {
            return this.databaseDir + "/data/" + tableName.split(" ")[0] + ".csv";
        }
        return null;
    }

    /**
     * Read the schema of the given table name from schema.txt
     * There are two types of supported input:
     * 1、Sailors S
     * 2、Sailor
     * The results of both types input returned are all the same
     *
     * @param tableName
     * @return schema
     */
    public List<String> getSchemaByTableName(String tableName) {
        // init operation
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.databaseDir + "/schema.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> list = Arrays.asList(line.trim().split(" "));
                this.schemaMap.put(list.get(0), list.subList(1, list.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.schemaMap.get(tableName.split(" ")[0]);
    }

    /**
     * Obtain the schema in the order of declaration of the SQL statement table
     * e.g.，
     * Sailors，return [A, B, C]
     * Sailors S，return [S.A, S.B, S.C]
     *
     * @param plainSelect
     * @return Schemas as a string list
     */
    public List<String> getSchemas(PlainSelect plainSelect) {
        List<String> schemas = new ArrayList<>();
        for (String tableName : getTableNameList(plainSelect)) {
            List<String> schema = getSchemaByTableName(tableName);
            // Determine whether an alias is used
            if (Util.useAliases(plainSelect)) {
                Util.addAlias2Schema(schema, tableName.split(" ")[1]);
            }
            schemas.addAll(schema);
        }
        return schemas;
    }

    /**
     * Return all tables in the SQL statement (in the order declared in FROM)
     * For example,
     * SELECT * FROM Boats, Reserves, Sailors WHERE Boats.D = Reserves.H -> [Boats, Reserves, Sailors]
     * If it contains an alias, it will return to table name + alias
     * 如SELECT * FROM Boats B, Reserves R -> [Boats B, Reserves R]
     *
     * @param plainSelect
     * @return table name list
     */
    public static List<String> getTableNameList(PlainSelect plainSelect) {
        List<String> tableNameList = new ArrayList<>();
        String tableName = plainSelect.getFromItem().toString();
        List<Join> joins = plainSelect.getJoins();
        tableNameList.add(tableName);
        if (joins != null) {
            for (Join join : joins) {
                tableNameList.add(join.toString());
            }
        }
        return tableNameList;
    }


    /**
     * Set the database path
     *
     * @param databaseDir
     */
    public void setDatabaseDir(String databaseDir) {
        this.databaseDir = databaseDir;


    }

    /**
     * Get the singleton instance
     *
     * @return Catlog object
     */
    public static Catlog getInstance() {
        return catlog;
    }
}
