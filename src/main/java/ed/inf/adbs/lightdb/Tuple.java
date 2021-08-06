package ed.inf.adbs.lightdb;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuple class can handle tuples as objects
 */
public class Tuple {

    private List<Integer> list = new ArrayList<>();

    /**
     * Use a string record to initialize tuple
     * @param record 101,2,3
     */
    public Tuple(String record) {
        for (String s : record.split(",")) {
            list.add(Integer.valueOf(s));
        }
    }

    public Tuple(List<Integer> list) {
        this.list = list;
    }

    /**
     * Add left and right tuple together
     * e.g., left=[1,2,3], right=[4,5,6], result=[1,2,3,4,5,6]
     * @param left tuple
     * @param right tuple
     * @return A new tuple
     */
    public static Tuple add(Tuple left, Tuple right) {
        List<Integer> ret = left.getAll();
        ret.addAll(right.getAll());
        return new Tuple(ret);
    }

    /**
     * Get a number from the tuple by index
     * @param index int, like 0,1,2..
     * @return A number
     */
    public Integer get(int index) {
        return list.get(index);
    }

    /**
     * Get the tuple internal structure
     * @return A list
     */
    public List<Integer> getAll() {
        return new ArrayList<>(this.list);
    }

    /**
     * Two tuples are equal if：
     * 1、Two tuples have the same size
     * 2、All corresponding positions of two tuples are equal
     * @param obj A tuple
     * @return true or false, if true two tuples are equal, otherwise unequal
     */
    @Override
    public boolean equals(Object obj) {
        List<Integer> l1 = this.getAll();
        List<Integer> l2 = ((Tuple) obj).getAll();
        if (l1.size() == l2.size()) {
            for (int i = 0; i < l1.size(); i++) {
                if (!l1.get(i).equals(l2.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Output tuples according to the given format
     * e.g., 1,2,3
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer i : list) {
            sb.append(i + ",");
        }
        return sb.substring(0, sb.length() - 1);
    }


}
