package syslog;


import akka.MessageHandler;

public class AlertPublishWorker extends MessageHandler<AlertPublishMessage> {

    @Override
    public void handle(AlertPublishMessage message) throws Exception {
        AlertSyslogActor actor = (AlertSyslogActor) getActor();
        for (Destination destination : actor.getDestinations()) {
            destination.publish(message.getAlert());
        }
    }


}