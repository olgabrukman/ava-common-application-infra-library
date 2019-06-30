package validation;

public class IntegerUtil {
    static public boolean equalsIgnoreNull(Integer a, Integer b) {
        if ((a == null) && (b == null)) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        return a.intValue() == b.intValue();
    }
}
