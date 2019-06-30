package logger;


import exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    private final Logger logger;

    public static AppLogger getLogger(String loggerName) {
        return new AppLogger(loggerName);
    }

    public static AppLogger getLogger(Class logClass) {
        return new AppLogger(logClass);
    }

    private AppLogger(String loggerName) {
        logger = LoggerFactory.getLogger(loggerName);
    }

    private AppLogger(Class logClass) {
        logger = LoggerFactory.getLogger(logClass);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void error(String message, Object p1) {
        logger.error(message, p1);
    }

    public void error(String message, Object p1, Object p2) {
        logger.error(message, p1, p2);
    }

    public void error(String message, Throwable e) {
        message = addDetails(message, e);
        logger.error(message, e);
    }

    public void error(String message, Object... parameters) {
        logger.error(message, parameters);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void warn(String message, Object p1) {
        logger.warn(message, p1);
    }

    public void warn(String message, Object p1, Object p2) {
        logger.warn(message, p1, p2);
    }

    public void warn(String message, Throwable e) {
        message = addDetails(message, e);
        logger.warn(message, e);
    }

    public void warn(String message, Object... parameters) {
        logger.warn(message, parameters);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Object p1) {
        logger.info(message, p1);
    }

    public void info(String message, Object p1, Object p2) {
        logger.info(message, p1, p2);
    }

    public void info(String message, Throwable e) {
        message = addDetails(message, e);
        logger.info(message, e);
    }

    public void info(String message, Object... parameters) {
        logger.info(message, parameters);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String message, Object p1) {
        logger.debug(message, p1);
    }

    public void debug(String message, Object p1, Object p2) {
        logger.debug(message, p1, p2);
    }

    public void debug(String message, Throwable e) {
        message = addDetails(message, e);
        logger.debug(message, e);
    }

    public void debug(String message, Object... parameters) {
        logger.debug(message, parameters);
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void trace(String message, Object p1) {
        logger.trace(message, p1);
    }

    public void trace(String message, Object p1, Object p2) {
        logger.trace(message, p1, p2);
    }

    public void trace(String message, Throwable e) {
        message = addDetails(message, e);
        logger.trace(message, e);
    }

    public void trace(String message, Object... parameters) {
        logger.trace(message, parameters);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    private String addDetails(String message, Throwable e) {
        if (!(e instanceof AppException)) {
            return message;
        }
        AppException AppException = (AppException) e;
        if (AppException.getLogObjects().isEmpty()) {
            return message;
        }
        StringBuilder additionalDetails = new StringBuilder("\nAdditional details:\n");
        boolean first = true;
        for (Object detail : AppException.getLogObjects()) {
            if (first) {
                first = false;
            } else {
                additionalDetails.append("\n");
            }
            if (detail == null) {
                detail = "(NULL)";
            }
            additionalDetails.append(detail.toString());
        }
        if (message == null) {
            return additionalDetails.toString();
        }
        return message + additionalDetails;
    }

}
