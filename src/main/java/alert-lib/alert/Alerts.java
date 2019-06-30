package alert;


import observers.AlertsObserver;

public interface Alerts extends AlertsObserver {
    void addAlertsApiObserver(AlertDestination alertDestination, AlertsObserver alertsApiObserver);
}
