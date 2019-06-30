package validation;

public class LongUtil {
    public static boolean isLessThanUnsigned(long small, long large) {
        return (small < large) ^ ((small < 0) != (large < 0));
    }

    public static boolean isLessThanOrEqualUnsigned(long small, long large) {
        return (small == large) || isLessThanUnsigned(small, large);
    }
}
