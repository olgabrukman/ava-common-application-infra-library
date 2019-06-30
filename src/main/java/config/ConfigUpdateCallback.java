package config;

public interface ConfigUpdateCallback {
    void handlePostValueUpdate(String key, String value) throws Exception;
}
