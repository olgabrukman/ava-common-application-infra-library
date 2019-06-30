package collection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

public class SetUtil {
    public static String getNiceList(Set set) {
        LinkedList<String> items = new LinkedList<>();
        for (Object item : set) {
            items.add(item.toString());
        }
        Collections.sort(items);
        StringBuilder result = new StringBuilder();
        for (String item : items) {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(item);
        }
        return result.toString();
    }
}
