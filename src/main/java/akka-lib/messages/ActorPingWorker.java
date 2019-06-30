package messages;

import akka.MessageHandlerWithResponse;

public class ActorPingWorker extends MessageHandlerWithResponse<ActorPingMessage> {
    @Override
    protected Object getResponse(ActorPingMessage message) throws Exception {
        return  getActor().pong();
    }
}
