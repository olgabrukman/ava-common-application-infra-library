package alert;

import logger.AppLogger;

public class AlertLog {
    // NOTICE - this class is referenced in log4j config file - do NOT rename
    private static final AppLogger logger = AppLogger.getLogger(AlertLog.class);

    static public void debug(String message) throws Exception {
        logger.debug(message);
    }

    static public void info(String message) throws Exception {
        logger.info(message);
    }

    static public void warn(String message) throws Exception {
        logger.warn(message);
    }

    static public void error(String message) throws Exception {
        logger.error(message);
    }
}
