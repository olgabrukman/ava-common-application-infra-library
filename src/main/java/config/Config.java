package config;

import actor.ConfigActor;
import actor.ConfigUpdateMessage;
import akka.ActorsManager;
import exception.AppException;
import logger.AppLogger;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import osgi.OsgiUtil;
import time.TimeUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.*;

public class Config {
    public static final String FILE_NAME_CONFIG = "app.config.properties";
    public static final String FILE_NAME_BASELINE = "app.baseline.properties";
    public static final String PATH_SOURCE = "package/karaf-assembly/src/main/resources";
    public static final String PATH_SOURCE_ETC = PATH_SOURCE + "/etc";

    static private final AppLogger logger = AppLogger.getLogger(Config.class);

    static private Config instance;

    private PropertiesConfiguration propertiesConfiguration;
    private PropertiesConfigurationLayout propertiesConfigurationLayout;
    private boolean isDebugEnvironment;
    private String configPath;
    private String rootPath;
    private String projectHome;
    private HashMap<String, ConfigUpdateCallback> callbacks;

    private Config(String fileName) throws Exception {
        callbacks = new HashMap<>();
        propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfigurationLayout = new PropertiesConfigurationLayout(propertiesConfiguration);

        String karafHome = checkLocal();
        if (karafHome != null) {
            rootPath = karafHome;
            String path = karafHome + "/etc/" + fileName;
            isDebugEnvironment = false;
            loadFile(path);
            return;
        }

        isDebugEnvironment = true;
        locateProjectHome();
        rootPath = projectHome + "/" + PATH_SOURCE;
        String path = projectHome + "/" + PATH_SOURCE_ETC + "/" + fileName;
        loadFile(path);
        path = projectHome + "/" + fileName;
        if (new File(path).exists()) {
            updateConfiguration(path);
        }
    }

    static public synchronized Config getInstance() throws Exception {
        if (instance == null) {
            instance = new Config(FILE_NAME_CONFIG);
        }
        return instance;
    }

    synchronized public void setUpdateCallback(String key, ConfigUpdateCallback callback) throws Exception {
        callbacks.put(key, callback);
    }


    private String checkLocal() {
        String workingFolder = System.getProperty("user.dir");
        if (new File(workingFolder + "/etc/" + FILE_NAME_CONFIG).exists()) {
            return workingFolder;
        }
        return null;
    }

    private void loadFile(String path) throws Exception {
        logger.debug("using app configuration file: " + path);
        try (FileReader reader = new FileReader(path)) {
            propertiesConfigurationLayout.load(reader);
            configPath = path;
            ConfigUpdate configUpdate = new ConfigUpdate(this);
            Thread thread = new Thread(configUpdate);
            thread.setDaemon(true);
            thread.start();
        }
    }

    synchronized private void updateConfiguration(String path) throws Exception {
        logger.debug("update configuration from: " + path);
        try (FileReader reader = new FileReader(path)) {
            // overriding settings by local configuration
            Properties properties = new Properties();
            properties.load(reader);
            for (Map.Entry entry : properties.entrySet()) {
                String value = entry.getValue().toString();
                value = value.replace(",", "\\,");
                value = value.replace("\n", "\\n");
                propertiesConfiguration.setProperty(entry.getKey().toString(), value);
            }
        }
    }

    private void locateProjectHome() throws Exception {
        String startupFolder = System.getProperty("user.dir");
        File currentFolder = new File(startupFolder);
        while (true) {
            String path = currentFolder + "/" + "PATH_SOURCE_FULL";
            if (new File(path).exists()) {
                projectHome = currentFolder.getAbsolutePath();
                return;
            }

            File parent = currentFolder.getParentFile();
            if (parent == null) {
                throw new AppException("unable to locate configuration file, starting from folder " + startupFolder);
            }
            currentFolder = parent;
        }
    }

    public String getRootPath() {
        return rootPath;
    }

    synchronized private void setProperty(String name, String value) throws Exception {
        validate(name, value);
        String oldValue = getProperty(name);
        propertiesConfiguration.setProperty(name, value);
        boolean isUpdate = !oldValue.equals(value);

        if (isDebugEnvironment) {
            logger.info("not updating file in debug environment, only in memory");
        } else {
            try (FileWriter writer = new FileWriter(configPath)) {
                propertiesConfigurationLayout.save(writer);
            }
        }

        if (!isUpdate) {
            return;
        }
        ConfigUpdateCallback callback = callbacks.get(name);
        if (callback == null) {
            return;
        }

        logger.debug("run callback in actor to avoid deadlock");
        ConfigUpdateMessage message = new ConfigUpdateMessage(callback, name, value);
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        actorsManager.sendToActorUnique(ConfigActor.class, message);
    }

    private void validate(String name, String value) throws Exception {
        switch (name) {
            case "NAME_LEARNING_PERIOD":
                TimeUtil.parseTimePeriod(value);
                break;
        }
    }

    private void setPropertyInt(String name, int value) throws Exception {
        setProperty(name, Integer.toString(value));
    }

    private void setPropertyFloat(String name, float value) throws Exception {
        setProperty(name, Float.toString(value));
    }

    private void setPropertyBoolean(String name, boolean value) throws Exception {
        setProperty(name, Boolean.toString(value));
    }

    synchronized private String getProperty(String name) throws Exception {
        Object value = propertiesConfiguration.getProperty(name);
        if (value == null) {
            throw new AppException("property " + name + " not found");
        }
        if (value instanceof List) {
            List list = (List) value;
            Object first = list.get(0);
            throw new AppException("invalid list value for " + name + " value is " + value + " first element is: " + first);
        }
        return value.toString().trim();
    }

    private int getPropertyInt(String name) throws Exception {
        String value = getProperty(name);
        return Integer.parseInt(value);
    }

    private float getPropertyFloat(String name) throws Exception {
        String value = getProperty(name);
        return Float.parseFloat(value);
    }

    private boolean getPropertyBoolean(String name) throws Exception {
        String value = getProperty(name);
        return Boolean.parseBoolean(value);
    }

    public void checkUpdates() {
        try {
            checkUpdatesWorker();
        } catch (Throwable e) {
            logger.warn("check config updates failed", e);
        }
    }

    public void reload() throws Exception {
        updateConfiguration(configPath);
    }

    private void checkUpdatesWorker() throws Exception {
        WatchService service = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(configPath);
        path = path.getParent();
        path.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
        while (true) {
            WatchKey key = service.take();
            boolean isFileChanged = false;
            for (WatchEvent event : key.pollEvents()) {
                if (FILE_NAME_CONFIG.equals(event.context().toString())) {
                    isFileChanged = true;
                }
            }
            if (isFileChanged) {
                updateConfiguration(configPath);
            }
            if (!key.reset()) {
                break;
            }
        }
    }


    @SuppressWarnings("unchecked")
    private Iterator<String> getPropertiesIterator() {
        return propertiesConfiguration.getKeys();
    }

    synchronized public String getKeyDirect(String name) throws Exception {
        Object value = propertiesConfiguration.getProperty(name);
        if (value == null) {
            return null;
        }
        return value.toString();
    }


    public int getRestTimeout() {
        return 10;
    }

    public int getActorResponseTimeout() {
        return 5;
    }

    public List<String> getModifiedKeys() {
        return null;
    }

    public List<String> getAllKeys() {
        return null;
    }

    public String getLoggerSpecificFile() {
        return "";
    }

    public void setKeyDirect(String name, String value) {

    }

    public String getSyslogPipe() {
        return "";
    }
}
