package messages;

import akka.MessageHandlerWithResponse;
import akka.MessageRunData;

import java.util.LinkedList;

public class LastReceiveWorker extends MessageHandlerWithResponse<LastReceiveMessage> {
    @Override
    protected LastReceiveResponse getResponse(LastReceiveMessage message) {
        LinkedList<MessageRunData> lastRun = new LinkedList<>(getActor().getLastMessages());
        return new LastReceiveResponse(lastRun);
    }
}
