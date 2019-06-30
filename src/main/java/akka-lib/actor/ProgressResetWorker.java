package actor;

import akka.MessageHandler;

public class ProgressResetWorker extends MessageHandler<ProgressResetMessage> {
    @Override
    public void handle(ProgressResetMessage message) throws Exception {
        ProgressActor actor = (ProgressActor) getActor();
        actor.getProgresses().clear();
    }
}
