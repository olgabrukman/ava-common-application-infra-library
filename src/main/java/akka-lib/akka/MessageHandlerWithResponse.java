package akka;

import akka.actor.Status;
import logger.AppLogger;

abstract public class MessageHandlerWithResponse<T> extends MessageHandler<T> {
    private static final AppLogger logger = AppLogger.getLogger(MessageHandlerWithResponse.class);

    final public void handle(T message) throws Exception {
        try {
            Object response = getResponse(message);
            logger.debug("response from handler is {}", response);
            getActor().sendBack(response);
        } catch (Throwable e) {
            logger.debug("error in handler, sending back to client", e);
            getActor().sendBack(new Status.Failure(e));
        }
    }

    protected abstract Object getResponse(T message) throws Exception;
}
