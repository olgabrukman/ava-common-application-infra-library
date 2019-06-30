package exception;

import org.apache.commons.lang.exception.ExceptionUtils;

public class ExceptionUtil {

    public static String getFullStackTrace(Throwable e) {
        return ExceptionUtils.getFullStackTrace(e);
    }
}
