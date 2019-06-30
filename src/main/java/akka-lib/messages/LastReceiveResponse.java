package messages;

import akka.MessageRunData;

import java.util.List;

public class LastReceiveResponse {
    private List<MessageRunData> messages;

    public LastReceiveResponse(List<MessageRunData> messages) {
        this.messages = messages;
    }

    public List<MessageRunData> getMessages() {
        return messages;
    }
}

