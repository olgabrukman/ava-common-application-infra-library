package validation;

import java.util.Collection;

public class CollectionUtil {
    public static final String SEPARATOR = ",";

    public static String getNiceList(Collection<?> collection) {
        return getNiceList(collection, SEPARATOR);
    }

    public static String getNiceList(Collection<?> collection, String separator) {
        StringBuilder result = new StringBuilder();
        for (Object value : collection) {
            if (result.length() > 0) {
                result.append(separator);
            }
            result.append(value.toString());
        }
        return result.toString();
    }

    static public boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

}
