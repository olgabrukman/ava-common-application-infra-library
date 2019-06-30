package observers;


import alert.Alert;
import alert.AlertLog;

/**
 * Handles alerts by sending them to log.
 */
public class AlertsObserverLog implements AlertsObserver {
    @Override
    public void handleAlert(Alert alert) throws Exception {
        switch (alert.getSeverity()) {
            case DEBUG:
                AlertLog.debug(alert.getFormattedMessage());
                break;
            case INFO:
                AlertLog.info(alert.getFormattedMessage());
                break;
            case WARNING:
                AlertLog.warn(alert.getFormattedMessage());
                break;
            case ERROR:
            case FATAL:
                AlertLog.error(alert.getFormattedMessage());
                break;
            default:
                break;
        }
    }
}
