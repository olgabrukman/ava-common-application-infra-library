package exception;

import java.util.LinkedList;
import java.util.List;

public class AppException extends RuntimeException {
    private List<Object> logObjects = new LinkedList<>();

    public static AppException addLogObject(Throwable e, Object object) {
        AppException AppException = wrapIfRequired(e);
        AppException.addLogObject(object);
        return AppException;
    }

    public static AppException wrapIfRequired(Throwable e) {
        if (e instanceof AppException) {
            return (AppException) e;
        }
        return new AppException(e);
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(Throwable e) {
        super(e);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public void addLogObject(Object o) {
        logObjects.add(o);
    }

    public List<Object> getLogObjects() {
        return logObjects;
    }
}
