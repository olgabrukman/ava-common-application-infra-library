package actor;


import config.ConfigUpdateCallback;

public class ConfigUpdateMessage {
    private final ConfigUpdateCallback callback;
    private final String name;
    private final String value;

    public ConfigUpdateMessage(ConfigUpdateCallback callback, String name, String value) {
        this.callback = callback;
        this.name = name;
        this.value = value;
    }

    public ConfigUpdateCallback getCallback() {
        return callback;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConfigUpdateMessage{" +
                "callback=" + callback +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
