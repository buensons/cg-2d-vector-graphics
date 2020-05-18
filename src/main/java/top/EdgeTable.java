package top;

import java.util.*;

public class EdgeTable {

    private Map<Integer, List<EdgeEntry>> table;

    public EdgeTable() {
        table = new HashMap<>();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public boolean containsKey(int key) {
        return table.containsKey(key);
    }

    public void add(int key, EdgeEntry entry) {
        if(table.containsKey(key)) {
            table.get(key).add(entry);
        } else {
            var list = new ArrayList<EdgeEntry>();
            list.add(entry);
            table.put(key, list);
        }
    }

    public List<EdgeEntry> remove(int key) {
        return table.remove(key);
    }

    public int size() {
        return table.size();
    }

}
