package akka;

import logger.AppLogger;
import messages.RetryingMessage;
import resource.MessageApi;

abstract public class MessageHandlerRetrying<T extends RetryingMessage> extends MessageHandler<T> {
    private static final AppLogger logger = AppLogger.getLogger(MessageHandlerRetrying.class);

    abstract protected void doUnstableWork() throws Exception;

    final public void handle(T message) throws Exception {
        try {
            doUnstableWork();
            logger.debug("handler success, retrying no longer required");
        } catch (Throwable e) {
            handleError(message, e);
        }
    }

    private void handleError(T message, Throwable e) {
        int maxTimes = 5;
        message.setTimes(message.getTimes() + 1);
        if (message.getTimes() > maxTimes) {
            // failed, aborting
            String errorMessage = MessageApi.getResource("app00114",
                    "ACTION", this.getClass().getName(),
                    "TIMES", message.getTimes());

            logger.warn(errorMessage, e);
            return;
        }

        int retryInSeconds = 10;
        // failed, retrying
        String errorMessage = MessageApi.getResource("app00113",
                "ACTION", this.getClass().getName(),
                "TIMES", message.getTimes(),
                "SECONDS", retryInSeconds);

        logger.warn(errorMessage, e);
        getActor().scheduleNextMilliseconds(message, retryInSeconds * 1000);
    }
}
