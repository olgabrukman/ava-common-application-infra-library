package logger;

// NOTICE: do not move/renamed - used by logger configuration file
public class AuditLog {
    static private final AppLogger logger = AppLogger.getLogger(AuditLog.class);

    public static void audit(String message) throws Exception {
        logger.info(message);
    }

}
