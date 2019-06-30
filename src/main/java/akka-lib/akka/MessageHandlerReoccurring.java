package akka;

import logger.AppLogger;
import messages.ReoccurringMessage;
import resource.MessageApi;

abstract public class MessageHandlerReoccurring<T extends ReoccurringMessage> extends MessageHandler<T> {
    private static final AppLogger logger = AppLogger.getLogger(MessageHandlerReoccurring.class);

    private boolean isAlive = true;

    abstract protected void handleReoccurring(T message) throws Exception;

    abstract protected int getIntervalSeconds() throws Exception;

    protected boolean isRoundToNearestInterval() throws Exception {
        return false;
    }

    final public void handle(T message) throws Exception {
        try {
            handleReoccurring(message);
        } finally {
            if (isAlive) {
                scheduleReoccurringOnce(message);
            } else {
                logger.debug("got killed");
            }
        }
    }

    // Schedule a message to be sent in a specific time in the future.
    // If this time has already passed, schedule it to the next time interval.
    public void scheduleReoccurringOnce(ReoccurringMessage message) throws Exception {
        AbstractActor actor = getActor();
        long timeToExecute;
        if (message.getTimeToExecute() == null) {
            timeToExecute = System.currentTimeMillis();
        } else {
            timeToExecute = message.getTimeToExecute();
        }

        int interval = getIntervalSeconds() * 1000;
        if (interval <= 0) {
            // invalid interval
            throw MessageApi.getException("app00867",
                    "HANDLER", this.getClass().getName());
        }

        timeToExecute += interval;
        if (isRoundToNearestInterval()) {
            timeToExecute = (timeToExecute / interval) * interval;
        }

        long now = System.currentTimeMillis();
        // If time passed, calculate next time frame
        while (timeToExecute < now) {
            timeToExecute += interval;
        }
        message.setTimeToExecute(timeToExecute);

        long milliseconds = timeToExecute - now;
        actor.scheduleNextMilliseconds(message, milliseconds);
    }

    public void killMe() {
        isAlive = false;
    }


}

