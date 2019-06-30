package messages;

import akka.MessageHandlerWithResponse;
import akka.MessageStatistics;

import java.util.HashMap;

public class StatisticsRequestWorker extends MessageHandlerWithResponse<StatisticsRequestMessage> {

    @Override
    protected StatisticsResponseMessage getResponse(StatisticsRequestMessage message) throws Exception {
        HashMap<String, MessageStatistics> statistics = getActor().getStatistics();
        HashMap<String, MessageStatistics> cloned = new HashMap<>();
        for (String key : statistics.keySet()) {
            MessageStatistics messageStatistics = statistics.get(key);
            MessageStatistics clonedMessageStatistics = messageStatistics.deepClone();
            cloned.put(key, clonedMessageStatistics);
        }
        return new StatisticsResponseMessage(getActor().getFriendlyName(), cloned);
    }
}
