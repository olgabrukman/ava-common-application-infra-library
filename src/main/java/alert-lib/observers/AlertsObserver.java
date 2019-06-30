package observers;


import alert.Alert;

public interface AlertsObserver {

    void handleAlert(Alert alert) throws Exception;
}
