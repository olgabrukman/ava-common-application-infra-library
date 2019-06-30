package observers;


import akka.ActorsManager;
import alert.Alert;
import osgi.OsgiUtil;
import syslog.AlertPublishMessage;
import syslog.AlertSyslogActor;

public class AlertsObserverSyslog implements AlertsObserver {

    @Override
    public void handleAlert(Alert alert) throws Exception {
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        AlertPublishMessage message = new AlertPublishMessage(alert);
        actorsManager.sendToActorUnique(AlertSyslogActor.class, message);
    }
}
