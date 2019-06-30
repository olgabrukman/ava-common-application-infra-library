package actor;


import akka.MessageHandler;

public class ConfigUpdateWorker extends MessageHandler<ConfigUpdateMessage> {
    @Override
    public void handle(ConfigUpdateMessage message) throws Exception {
        message.getCallback().handlePostValueUpdate(message.getName(), message.getValue());
    }
}
