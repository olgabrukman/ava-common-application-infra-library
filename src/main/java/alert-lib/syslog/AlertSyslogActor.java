package syslog;

import akka.AbstractActor;

import java.util.LinkedList;
import java.util.List;

public class AlertSyslogActor extends AbstractActor {

    private List<Destination> destinations = new LinkedList<>();

    @Override
    protected void addHandlers() throws Exception {
        addHandler(DestinationsMessage.class, DestinationsWorker.class);
        addHandler(AlertPublishMessage.class, AlertPublishWorker.class);
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }
}
