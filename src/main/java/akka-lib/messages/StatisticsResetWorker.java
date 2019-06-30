package messages;

import akka.MessageHandler;

public class StatisticsResetWorker extends MessageHandler<StatisticsResetMessage> {
    @Override
    public void handle(StatisticsResetMessage message) {
        getActor().resetStatistics();
    }
}
