package syslog;

import java.util.List;

public class DestinationsMessage {

    private List<Destination> destinations;

    public DestinationsMessage(List<Destination> destinations) {
        this.destinations = destinations;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    @Override
    public String toString() {
        return "DestinationsMessage{" +
                "destinations=" + destinations +
                '}';
    }
}
