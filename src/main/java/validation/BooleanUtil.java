package validation;

public class BooleanUtil {
    @SuppressWarnings("all")
    static public boolean equalsIgnoreNull(Boolean a, Boolean b) {
        if ((a == null) && (b == null)) {
            return true;
        }
        if ((a == null) || (b == null)) {
            return false;
        }
        return a.booleanValue() == b.booleanValue();

    }
}
