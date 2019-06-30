package alert;

import org.apache.commons.csv.CSVRecord;
import resource.MessageApi;
import resource.MessageResource;

import java.util.*;

public class Alert {
    private String id;
    private AlertSeverity severity;
    private Map<String, Object> parameters;
    private String userMessage;
    private HashSet<AlertDestination> destinations;

    public Alert(String id) {
        this.id = id;
        destinations = new HashSet<>();
        parameters = new HashMap<>();
        severity = AlertSeverity.DEBUG;
    }

    public Alert(Alert that) {
        id = that.id;
        destinations = new HashSet<>();
        destinations.addAll(that.destinations);
        parameters = new HashMap<>(that.parameters);
        severity = that.severity;
    }

    public Alert(CSVRecord csvRecord) {
        Iterator<String> currentRecordIterator = csvRecord.iterator();
        this.id = currentRecordIterator.next();
        this.severity = AlertSeverity.valueOf(currentRecordIterator.next());
        destinations = new HashSet<>();
        parameters = new HashMap<>();
        String destinationsString = currentRecordIterator.next();
        StringTokenizer tokenizer = new StringTokenizer(destinationsString, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            AlertDestination alertDestination = AlertDestination.valueOf(token);
            destinations.add(alertDestination);
        }
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public HashSet<AlertDestination> getDestinations() {
        return destinations;
    }

    public String getFormattedMessage() {
        return "[" + id + "] " + getFormattedMessageWithoutId();
    }

    public String getFormattedMessageWithoutId() {
        MessageResource messageResource;
        if (userMessage == null) {
            messageResource = MessageApi.getMessageResource(id);
        } else {
            messageResource = new MessageResource(id, userMessage);
        }
        for (String name : parameters.keySet()) {
            Object value = parameters.get(name);
            messageResource.setParameter(name, value);
        }
        return messageResource.getText();
    }


    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public void setParameters(Map<String, String> parameters) {
        for (String name : parameters.keySet()) {
            setParameter(name, parameters.get(name));
        }
    }

    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }


    public String getId() {
        return id;
    }
}
