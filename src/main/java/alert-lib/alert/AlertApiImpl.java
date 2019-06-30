package alert;


import observers.AlertsObserverCli;
import observers.AlertsObserverLog;
import observers.AlertsObserverSyslog;

import java.util.AbstractMap;

public class AlertApiImpl implements AlertApi {
    public static final String ALERTS_CONFIG_FILE_NAME = "etc/alerts.csv";

    private AlertsContainer alertsContainer;
    private AlertsImpl alertsImpl = new AlertsImpl();

    public AlertApiImpl() throws Exception {
        String appHome = "";
        AlertsFactory alertsFactory = new AlertsFactory(appHome + "/" + ALERTS_CONFIG_FILE_NAME);
        alertsContainer = alertsFactory.createAllAlerts();
        alertsImpl.addAlertsApiObserver(AlertDestination.LOG, new AlertsObserverLog());
        alertsImpl.addAlertsApiObserver(AlertDestination.SYSLOG, new AlertsObserverSyslog());
        alertsImpl.addAlertsApiObserver(AlertDestination.CLI, AlertsObserverCli.getInstance());
    }

    @Override
    public Alert alertFetch(String alertId) throws Exception {
        return new Alert(alertsContainer.getAlertById(alertId));
    }

    @Override
    public void alertRaise(Alert alert) {
        alertsImpl.handleAlert(alert);
    }

    @Override
    public void alertRaise(String alertId, String userMessage) throws Exception {
        Alert alertToRaise = alertsContainer.getAlertById(alertId);
        alertToRaise.setUserMessage(userMessage);
        alertsImpl.handleAlert(alertToRaise);
    }

    @Override
    public AbstractMap<String, Alert> alertsList() {
        return alertsContainer.getAlerts();
    }
}

