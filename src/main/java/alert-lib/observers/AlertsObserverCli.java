package observers;


import alert.Alert;
import alert.AlertSeverity;
import logger.AppLogger;
import org.apache.felix.service.command.CommandSession;
import validation.EnumValidation;

import java.util.HashMap;
import java.util.HashSet;

public class AlertsObserverCli implements AlertsObserver {
    private static final AppLogger logger = AppLogger.getLogger(AlertsObserverCli.class);

    static private AlertsObserverCli myInstance;

    private HashMap<CommandSession, AlertSeverity> sessions = new HashMap<>();

    synchronized static public AlertsObserverCli getInstance() {
        if (myInstance == null) {
            myInstance = new AlertsObserverCli();
        }
        return myInstance;
    }

    private AlertsObserverCli() {
    }

    @Override
    synchronized public void handleAlert(Alert alert) throws Exception {
        String formattedMessage = alert.getFormattedMessage();

        HashSet<CommandSession> toRemove = new HashSet<>();

        for (CommandSession session : sessions.keySet()) {
            AlertSeverity sessionSeverity = sessions.get(session);
            logger.debug("handling alert {} severity {} session severity {}",
                    alert.getId(), alert.getSeverity(), sessionSeverity);
            if (alert.getSeverity().getLevel() >= sessionSeverity.getLevel()) {
                try {
                    session.getConsole().println(formattedMessage);
                    session.getConsole().flush();
                } catch (Throwable e) {
                    logger.debug("assume session is dead, removing session", e);
                    toRemove.add(session);
                }
            }
        }

        for (CommandSession session : toRemove) {
            sessions.remove(session);
        }
    }

    synchronized public void update(CommandSession session, boolean add, String severity) throws Exception {
        AlertSeverity alertSeverity = AlertSeverity.DEBUG;
        if (severity != null) {
            alertSeverity = EnumValidation.validateEnumAndGet("severity", AlertSeverity.class, severity);
        }
        if (add) {
            sessions.put(session, alertSeverity);
        } else {
            sessions.remove(session);
        }
    }
}
