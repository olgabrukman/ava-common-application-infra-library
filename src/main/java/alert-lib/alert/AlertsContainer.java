package alert;

import resource.MessageApi;

import java.util.AbstractMap;
import java.util.HashMap;

public class AlertsContainer {

    private AbstractMap<String, Alert> alerts = new HashMap<>();

    public Alert getAlertById(String id) throws Exception {
        Alert alert = alerts.get(id);
        if (alert == null) {
            // alert not found
            throw MessageApi.getException("app00870",
                    "ALERT_ID", id);
        }

        return new Alert(alert);
    }

    public void setAlert(String id, Alert alertToSet) {
        alerts.put(id, alertToSet);
    }

    public AbstractMap<String, Alert> getAlerts() {
        return alerts;
    }
}
