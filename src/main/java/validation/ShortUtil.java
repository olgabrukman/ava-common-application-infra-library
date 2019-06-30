package validation;

public class ShortUtil {

    @SuppressWarnings("all")
    static public boolean equalsIgnoreNull(Short a, Short b) {
        if ((a == null) && (b == null)) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        return a.shortValue() == b.shortValue();
    }
}
