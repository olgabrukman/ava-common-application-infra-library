package config;

public enum MonitoredParameter {

    CPU("cpu"),
    BANDWIDTH("bandwidth"),
    NUMBER_OF_POLICIES("policies.number"),

    MAX_POLICIES("max.policies"),
    MAX_BANDWIDTH("max.bandwidth");

    private String configProperty;

    MonitoredParameter(String configProperty) {
        this.configProperty = configProperty;
    }

    public String getConfigProperty() {
        return configProperty;
    }

}
