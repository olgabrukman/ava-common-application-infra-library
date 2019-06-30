package messages;

import akka.MessageStatistics;

import java.util.HashMap;

public class StatisticsResponseMessage {
    private String actorName;
    private HashMap<String, MessageStatistics> statistics;

    public StatisticsResponseMessage(String actorName, HashMap<String, MessageStatistics> statistics) {
        this.actorName = actorName;
        this.statistics = statistics;
    }

    public String getActorName() {
        return actorName;
    }

    public HashMap<String, MessageStatistics> getStatistics() {
        return statistics;
    }
}

