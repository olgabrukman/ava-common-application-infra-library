package alert;

import java.util.AbstractMap;

public interface AlertApi {
    Alert alertFetch(String alertId) throws Exception;

    void alertRaise(Alert alert) throws Exception;

    //userMessage - The final alert message that will appear in the alert. Override any alerts parameters (if exist).
    void alertRaise(String alertId, String userMessage) throws Exception;

    AbstractMap<String, Alert> alertsList();
}
