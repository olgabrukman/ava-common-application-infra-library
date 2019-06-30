package resource;

import java.util.HashMap;

public class MessageResource {
    private final String id;
    private final String resource;
    private final HashMap<String, String> parameters;

    public MessageResource(String id, String resource) {
        this.id = id;
        this.resource = resource;
        parameters = new HashMap<>();
    }

    public void setParameter(String name, Object value) {
        if (value == null) {
            value = "(null)";
        }
        parameters.put(name, value.toString());
    }

    public String getText() {
        String text;
        if (resource == null) {
            text = getTextForMissingResource();
        } else {
            text = getTextForExistingResource();
        }
        text = text.replace("\\n", "\n");
        return text;
    }

    private String getTextForExistingResource() {
        String message = resource;
        for (String name : parameters.keySet()) {
            String value = parameters.get(name);
            String search = "{" + name + "}";
            message = message.replace(search, value);
        }
        return message;
    }

    private String getTextForMissingResource() {
        StringBuilder builder = new StringBuilder();
        builder.append("Missing Resource(");
        builder.append(id);
        builder.append(") ");
        boolean isFirst = true;
        for (String name : parameters.keySet()) {
            String value = parameters.get(name);
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }
            builder.append(name);
            builder.append("=");
            builder.append(value);
        }
        return builder.toString();
    }
}
