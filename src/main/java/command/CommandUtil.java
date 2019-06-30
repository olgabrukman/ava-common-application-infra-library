package command;


import io.Encoding;
import logger.AppLogger;
import org.apache.commons.io.IOUtils;

public class CommandUtil {
    private static final AppLogger logger = AppLogger.getLogger(CommandUtil.class);

    static public String runCommandAndGetOutput(String commandLine) throws Exception {
        logger.debug("running command {}", commandLine);
        Process process = Runtime.getRuntime().exec(commandLine);
        logger.debug("process started");
        return runProcessAndGetResult(process);
    }

    private static String runProcessAndGetResult(Process process) throws Exception {
        process.waitFor();
        logger.debug("wait for process done");
        String allOutput = IOUtils.toString(process.getInputStream(), Encoding.ENCODING);
        logger.debug("stdout collected");
        allOutput += IOUtils.toString(process.getErrorStream(), Encoding.ENCODING);
        logger.debug("command output is {}", allOutput);
        return allOutput;
    }

    static public String runCommandWithPipesAndGetOutput(String commandLine) throws Exception {
        logger.debug("running command using pipes {}", commandLine);
        String[] commands = {
                "/bin/sh",
                "-c",
                commandLine
        };
        Process process = Runtime.getRuntime().exec(commands);
        return runProcessAndGetResult(process);
    }
}
