package ed.inf.adbs.lightdb.operator;

import ed.inf.adbs.lightdb.utils.Catlog;
import ed.inf.adbs.lightdb.Tuple;

import java.io.*;

/**
 * Scan operator is used to full table scans
 */
public class ScanOperator extends Operator {

    // This filed is used to store the table path
    private String file;

    private BufferedReader reader = null;

    public ScanOperator(String tableName) {
        String file = Catlog.getInstance().getFileByTableName(tableName);
        this.file = file;
        try {
            this.reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the next tuple of this operator
     * @return next tuple, if available, otherwise return null
     */
    @Override
    public Tuple getNextTuple() {
        Tuple tuple = null;
        try {
            String line = this.reader.readLine();
            if (line != null) {
                tuple = new Tuple(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tuple;
    }

    /**
     * Reset the operator
     */
    @Override
    public void reset() {
        try {
            this.reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
