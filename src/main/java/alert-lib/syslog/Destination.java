package syslog;

import alert.Alert;
import alert.AlertSeverity;
import logger.AppLogger;

public class Destination {
    static private final AppLogger logger = AppLogger.getLogger(Destination.class);

    private final String ip;
    private final int port;
    private final AlertSeverity severity;

    public Destination(String ip, int port, AlertSeverity severity) {
        this.ip = ip;
        this.port = port;
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", severity=" + severity +
                '}';
    }

    public void publish(Alert alert) {
    }
}
