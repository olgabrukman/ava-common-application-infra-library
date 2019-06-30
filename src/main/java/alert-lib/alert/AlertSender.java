package alert;

import osgi.OsgiUtil;
import resource.MessageApi;

public class AlertSender {
    static public String send(String alertId, Object... parameters) throws Exception {
        return sendConditionally(true, alertId, parameters);
    }

    static public String sendConditionally(boolean send, String alertId, Object... parameters) throws Exception {
        AlertApi alertApi = OsgiUtil.getService(AlertApi.class);
        Alert alert = alertApi.alertFetch(alertId);
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
            String name = (String) parameters[parameterIndex];
            parameterIndex++;
            if (parameterIndex >= parameters.length) {
                // invalid parameters amount
                throw MessageApi.getException("app00871");
            }
            Object value = parameters[parameterIndex];
            alert.setParameter(name, value);
        }
        if (send) {
            alertApi.alertRaise(alert);
        }
        return alert.getFormattedMessageWithoutId();
    }
}