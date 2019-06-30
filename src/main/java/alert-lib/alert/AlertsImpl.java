package alert;

import logger.AppLogger;
import observers.AlertsObserver;
import resource.MessageApi;

import java.util.AbstractMap;
import java.util.HashMap;

public class AlertsImpl implements Alerts {
    private static final AppLogger logger = AppLogger.getLogger(AlertsImpl.class);

    private AbstractMap<AlertDestination, AlertsObserver> observers = new HashMap<>();

    @Override
    public void addAlertsApiObserver(AlertDestination alertDestination, AlertsObserver alertsApiObserver) {
        this.observers.put(alertDestination, alertsApiObserver);
    }

    @Override
    public void handleAlert(Alert alert) {
        try {
            handleAlertWorker(alert);
        } catch (Throwable e) {
            logger.warn("handle alert failed", e);
        }
    }

    private void handleAlertWorker(Alert alert) throws Exception {
        if (alert == null) {
            // null alert
            throw MessageApi.getException("app00868");
        }
        String alertMessage = alert.getFormattedMessage();
        if (alertMessage == null) {
            // empty message
            throw MessageApi.getException("app00869",
                    "ALERT_ID", alert.getId());
        }
        // Can't just send the alert to all observers - need to check destinations and send only where required:
        for (AlertDestination destination : alert.getDestinations()) {
            AlertsObserver observerApi = observers.get(destination);
            try {
                observerApi.handleAlert(alert);
            } catch (Throwable e) {
                String error = "Handler " + observerApi.getClass().getSimpleName() + " failed for alert " +
                        alert.getId();
                logger.warn(error, e);
            }
        }
    }

}
