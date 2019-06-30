package syslog;


import alert.Alert;

public class AlertPublishMessage {
    private Alert alert;

    public AlertPublishMessage(Alert alert) {
        this.alert = alert;
    }

    public Alert getAlert() {
        return alert;
    }

    @Override
    public String toString() {
        return "AlertPublishMessage{" +
                "alert=" + alert +
                '}';
    }

}
