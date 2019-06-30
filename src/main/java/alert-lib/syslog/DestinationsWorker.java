package syslog;

import akka.MessageHandler;

public class DestinationsWorker extends MessageHandler<DestinationsMessage> {

    @Override
    public void handle(DestinationsMessage message) throws Exception {
        AlertSyslogActor actor = (AlertSyslogActor) getActor();
        actor.setDestinations(message.getDestinations());
    }


}