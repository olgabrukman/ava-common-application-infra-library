package syslog;

import logger.AppLogger;

// NOTICE - this class is referenced in log4j config file - do NOT rename
public class SyslogPipeServerLogger {
    public static final AppLogger logger = AppLogger.getLogger(SyslogPipeServerLogger.class);
}
