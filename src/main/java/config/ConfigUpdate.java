package config;

public class ConfigUpdate implements Runnable {

    private Config config;

    public ConfigUpdate(Config config) {
        this.config = config;
    }


    public void run() {
        config.checkUpdates();
    }
}
