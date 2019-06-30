package akka;

import logger.AppLogger;

abstract public class MessageHandler<T> {
    private static final AppLogger logger = AppLogger.getLogger(MessageHandler.class);
    private AbstractActor abstractActor;

    public AbstractActor getActor() {
        return abstractActor;
    }

    void setActor(AbstractActor abstractActor) {
        this.abstractActor = abstractActor;
    }

    @SuppressWarnings("unchecked")
    public void deliver(Object message) {
        String handlerClassName = this.getClass().getSimpleName();
        String actorClassName = abstractActor.getClass().getSimpleName();
        logger.debug("{}:{} starting", actorClassName, handlerClassName);
        try {
            T typedMessage = (T) message;
            handle(typedMessage);
            abstractActor.handleStatistics(message, null);
        } catch (Throwable e) {
            logger.warn("actor " + actorClassName + " handler " + handlerClassName + " failed", e);
            abstractActor.handleStatistics(message, e);
        }
        logger.debug("{}:{} handled the message", actorClassName, handlerClassName);
    }

    abstract public void handle(T message) throws Exception;
}
