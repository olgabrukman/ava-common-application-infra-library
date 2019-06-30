package messages;

import akka.MessageHandler;

public class ActorInitWorker extends MessageHandler<ActorInitMessage> {
    @Override
    public void handle(ActorInitMessage message) throws Exception {
        getActor().internalInitialize(message);
    }
}
