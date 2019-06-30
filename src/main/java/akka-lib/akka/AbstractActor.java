package akka;

import akka.actor.UntypedActor;
import exception.AppException;
import logger.AppLogger;
import messages.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import osgi.OsgiUtil;
import resource.MessageApi;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

abstract public class AbstractActor extends UntypedActor {
    private static final AppLogger logger = AppLogger.getLogger(AbstractActor.class);

    public static boolean keepLastMessages = false;
    public static Set<Class> testMessagesClasses;
    private static LinkedList<Object> testMessagesLocated;

    private HashMap<String, Class<? extends MessageHandler>> handlers;

    private CircularFifoQueue<MessageRunData> lastMessages;
    private HashMap<String, MessageStatistics> statistics;
    private String friendlyNameSuffix;
    private long currentMessageReceiveTime;
    private String actorKey;

    abstract protected void addHandlers() throws Exception;

    public boolean isAutomaticKill() {
        return true;
    }

    public String getFriendlyName() {
        if (friendlyNameSuffix != null) {
            return this.getClass().getSimpleName() + " " + friendlyNameSuffix;
        }
        return this.getClass().getSimpleName();
    }

    public void setFriendlyNameSuffix(String friendlyNameSuffix) {
        this.friendlyNameSuffix = friendlyNameSuffix;
    }

    protected void addHandler(Class messageClass, Class<? extends MessageHandler> handlerClass){
        if (handlers == null) {
            handlers = new HashMap<>();
        }
        if (handlers.containsKey(messageClass.getName())) {
            throw new AppException(messageClass.getName() + " is already registered");
        }
        handlers.put(messageClass.getName(), handlerClass);
    }

    @Override
    final public void preStart() throws Exception {
        resetStatistics();
        addHandlers();
        addHandler(ActorInitMessage.class, ActorInitWorker.class);
        addHandler(ActorPingMessage.class, ActorPingWorker.class);
        addHandler(LastReceiveMessage.class, LastReceiveWorker.class);
        addHandler(StatisticsResetMessage.class, StatisticsResetWorker.class);
        addHandler(StatisticsRequestMessage.class, StatisticsRequestWorker.class);

        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        actorsManager.actorRegistration(this, true);
    }

    @Override
    public void postStop() throws Exception {
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        actorsManager.actorRegistration(this, false);
    }

    // override postRestart so we don't call preStart and schedule a new message
    @Override
    final public void postRestart(Throwable reason) {
    }

    @Override
    final public void onReceive(Object message) {
        currentMessageReceiveTime = System.currentTimeMillis();
        logger.debug("actor {} handling message {}", this.getContext().guardian().path().name(), message);

        if (keepLastMessages) {
            if (!(message instanceof InternalMessage)) {
                MessageRunData messageRunData = new MessageRunData(message.getClass());
                lastMessages.add(messageRunData);
            }
        }

        try {
            receiveWorker(message);
        } catch (Throwable e) {
            logger.warn("actor " + this.getClass().getName() + " handling message " +
                    message.getClass().getName() + " failed", e);
        }
    }


    public void handleStatistics(Object message, Throwable workerError) {
        try {
            handleStatisticsWorker(message, workerError);
        } catch (Throwable e) {
            logger.warn("failed handling statistics", e);
        }
    }

    private void handleStatisticsWorker(Object message, Throwable workerError) throws Exception {
        if (message instanceof InternalMessage) {
            return;
        }
        long endTime = System.currentTimeMillis();
        String key = message.getClass().getSimpleName();
        MessageStatistics messageStatistics = statistics.get(key);
        if (messageStatistics == null) {
            messageStatistics = new MessageStatistics();
            statistics.put(key, messageStatistics);
        }
        messageStatistics.update(currentMessageReceiveTime, endTime, workerError);
    }

    private void receiveWorker(Object message) throws Exception {
        if (handlers == null) {
            // add handlers not invoked
            throw MessageApi.getException("app00863");
        }
        Class<? extends MessageHandler> handlerClass = handlers.get(message.getClass().getName());
        if (handlerClass == null) {
            // missing handler
            throw MessageApi.getException("app00864",
                    "MESSAGE_CLASS", message.getClass().getName());
        }

        MessageHandler handler = handlerClass.newInstance();
        handler.setActor(this);
        handler.deliver(message);
        if (testMessagesClasses != null) {
            testUpdateMessages(message);
        }
    }

    synchronized static private void testUpdateMessages(Object message) {
        if (testMessagesClasses == null) {
            return;
        }
        if (!testMessagesClasses.contains(message.getClass())) {
            return;
        }
        testMessagesLocated.add(message);
    }

    synchronized static public LinkedList<Object> testGetMessages(boolean reset) {
        LinkedList<Object> result;
        if (testMessagesLocated == null) {
            result = new LinkedList<>();
        } else {
            result = new LinkedList<>(testMessagesLocated);
        }
        if (reset) {
            testMessagesLocated = new LinkedList<>();
        }
        return result;
    }

    synchronized static public void testStopMessages() {
        testMessagesLocated = null;
    }


    public void scheduleNextSeconds(Object message, int seconds) {
        scheduleNextMilliseconds(message, seconds * 1000L);
    }

    public void scheduleNextMilliseconds(Object message, long milliseconds) {
        FiniteDuration duration = Duration.create(milliseconds, TimeUnit.MILLISECONDS);
        getContext().system().scheduler().scheduleOnce(duration, getSelf(), message, getContext().dispatcher(), null);
        logger.debug("actor {} scheduled message {} for {} milliseconds ahead",
                this.getClass().getSimpleName(), message.getClass().getSimpleName(), milliseconds);
    }


    public void sendBack(Object message) {
        getSender().tell(message, getSelf());
    }

    public CircularFifoQueue<MessageRunData> getLastMessages() {
        return lastMessages;
    }

    public void resetStatistics() {
        statistics = new HashMap<>();
        lastMessages = new CircularFifoQueue<>(10);
    }

    public HashMap<String, MessageStatistics> getStatistics() {
        return statistics;
    }

    public void internalInitialize(ActorInitMessage message) {
        actorKey = message.getActorKey();
    }

    public String getActorKey() {
        return actorKey;
    }

    public Object pong() {
        return "context:" + this.context() + ", path:" + this.getSelf().path();
    }
}
