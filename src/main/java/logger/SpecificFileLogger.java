package logger;



import config.Config;

// NOTICE: do not move/rename - used by logger configuration file
public class SpecificFileLogger {
    public static AppLogger getLogger(String fileName) {
        String logger = SpecificFileLogger.class.getName() + "." + getLoggerName(fileName);
        return AppLogger.getLogger(logger);
    }

    public static void update(String fileName, boolean create) throws Exception {
        String header = "\n#LOG FILE START " + fileName + " - do not change this section\n";
        String trailer = "\n#LOG FILE END " + fileName + " - do not change this section\n";
        //KarafLoggerConfiguration configuration = new KarafLoggerConfiguration();
        String data = "";//configuration.get();
        int startPosition = data.indexOf(header);
        int endPosition = data.indexOf(trailer);
        if ((startPosition != -1) && (endPosition != -1)) {
            String prefix = data.substring(0, startPosition);
            String postfix = data.substring(endPosition + trailer.length());
            data = prefix + postfix;
        }
        if (create) {
            String addedConfiguration = header + Config.getInstance().getLoggerSpecificFile() + trailer;
            addedConfiguration = addedConfiguration.replace("\\n", "\n");
            addedConfiguration = addedConfiguration.replace("{LOGGER_NAME}", getLoggerName(fileName));
            addedConfiguration = addedConfiguration.replace("{FILE_NAME}", fileName);
            data += addedConfiguration;
        }
        //configuration.set(data);
    }

    private static String getLoggerName(String fileName) {
        String loggerName = fileName.replace("/", "_");
        loggerName = loggerName.replace(".", "_");
        loggerName = loggerName.replace(" ", "_");
        return loggerName;
    }
}
