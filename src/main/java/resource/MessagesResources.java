package resource;

import config.Config;
import io.FileUtil;
import logger.AppLogger;

import java.io.LineNumberReader;
import java.util.HashMap;

public class MessagesResources {

    private static final AppLogger logger = AppLogger.getLogger(MessagesResources.class);

    static private MessagesResources instance;

    private HashMap<String, String> resources;
    private boolean hasDuplicates;

    public MessagesResources() {
        hasDuplicates = false;
        resources = new HashMap<>();
        try {
            String appHome = Config.getInstance().getRootPath();
            String path = appHome + "/etc/app.messages.txt";
            logger.debug("loading resource {}", path);
            loadFile(path);
        } catch (Throwable e) {
            // we don't want user to handle our errors
            logger.warn("load resources failed", e);
        }
    }

    private void loadFile(String path) throws Exception {
        try (LineNumberReader reader = FileUtil.getLineNumberReader(path)) {
            int lineNumber = 0;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return;
                }
                lineNumber++;
                loadResource(line, lineNumber);
            }
        }
    }

    public static MessagesResources getInstance() {
        if (instance == null) {
            instance = new MessagesResources();
        }
        return instance;
    }

    private void loadResource(String line, int lineNumber) {
        line = line.trim();
        if (line.isEmpty()) {
            return;
        }

        int equalPosition = line.indexOf("=");
        if (equalPosition == -1) {
            logger.warn("invalid resource at line " + lineNumber);
            return;
        }
        String id = line.substring(0, equalPosition);
        String resource = line.substring(equalPosition + 1);
        if (resources.get(id) != null) {
            logger.warn("duplicate resource " + id + " at line " + lineNumber);
            hasDuplicates = true;
        }
        resources.put(id, resource);
    }

    public boolean hasDuplicates() {
        return hasDuplicates;
    }

    public MessageResource getResource(String id) {
        String resource = resources.get(id);
        return new MessageResource(id, resource);
    }
}
