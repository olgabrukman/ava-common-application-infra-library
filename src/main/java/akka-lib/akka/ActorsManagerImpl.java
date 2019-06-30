package akka;

import akka.actor.*;
import akka.pattern.Patterns;
import config.Config;
import exception.AppException;
import logger.AppLogger;
import messages.ActorInitMessage;
import resource.MessageApi;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ActorsManagerImpl implements ActorsManager {
    public static final String SEPARATOR = "_";
    private static final AppLogger logger = AppLogger.getLogger(ActorsManagerImpl.class);

    private ActorSystem actorSystem;
    private HashMap<String, ActorRef> actors;
    private HashMap<String, String> actorDispatcher;
    private HashSet<AbstractActor> activeActors;
    private boolean allowNewActors;

    public ActorsManagerImpl(ActorSystem system) {
        activeActors = new HashSet<>();
        actorSystem = system;
        actors = new HashMap<>();
        actorDispatcher = new HashMap<>();
        allowNewActors = true;
    }

    private static String actorKey(Class actorType, Object actorId) {
        if (actorId == null) {
            return actorType.getName();
        }
        return actorType.getName() + SEPARATOR + actorId;
    }

    @Override
    public void sendToActor(Class actorType, Object actorId, Object message) throws Exception {
        logger.debug("sending to actor type {}, actor id {}, message {}",
                actorType.getSimpleName(), actorId, message.getClass().getSimpleName());
        ActorRef actor = getActorReference(actorType, actorId, true);
        actor.tell(message, ActorRef.noSender());
    }

    @Override
    public void sendToActorUnique(Class actorType, Object message) throws Exception {
        logger.debug("sending to actor type {}, message {}",
                actorType.getSimpleName(), message.getClass().getSimpleName());
        ActorRef actor = getActorReference(actorType, null, true);
        actor.tell(message, ActorRef.noSender());
    }

    @Override
    public void sendToActorByPath(String actorFullPath, Object message) throws Exception {
        ActorRef actor = getActorByPath(actorFullPath);
        actor.tell(message, ActorRef.noSender());
    }

    private ActorRef getActorByPath(String actorFullPath) throws Exception {
        ActorRef actor = actors.get(actorFullPath);
        if (actor == null) {
            // actor path not found
            throw MessageApi.getException("app00866",
                    "PATH", actorFullPath);
        }
        return actor;
    }

    @Override
    public boolean doesActorExist(Class actorType, Object actorId) throws Exception {
        return actors.containsKey(actorKey(actorType, actorId));
    }

    public <T> T sendToActorUniqueAndReceive(Class actorType, Object message, Integer timeoutSeconds) throws Exception {
        String actorKey = actorKey(actorType, null);
        getActorReference(actorType, null, true);
        return sendAndReceive(message, timeoutSeconds, actorKey);
    }

    @Override
    public <T> T sendToActorByPathAndReceive(String actorFullPath, Object message, Integer timeoutSeconds) throws Exception {
        ActorSelection actorSelection = actorSystem.actorSelection(actorFullPath);
        logger.debug("selection for {} result is {}", actorFullPath, actorSelection);
        if (timeoutSeconds == null) {
            timeoutSeconds = Config.getInstance().getActorResponseTimeout();
        }
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) Patterns.ask(actorSelection, message, timeoutSeconds * 1000);
        FiniteDuration duration = Duration.create(timeoutSeconds, TimeUnit.SECONDS);
        return Await.result(future, duration);
    }

    @Override
    public <T> T sendToActorAndReceive(Class actorType, Object actorId, Object message, Integer timeoutSeconds) throws Exception {
        String actorKey = actorKey(actorType, actorId);
        getActorReference(actorType, actorId, true);
        return sendAndReceive(message, timeoutSeconds, actorKey);
    }

    private <T> T sendAndReceive(Object message, Integer timeoutSeconds, String actorKey) throws Exception {
        if (timeoutSeconds == null) {
            timeoutSeconds = Config.getInstance().getActorResponseTimeout();
        }
        Future<T> future = sendAndGetFuture(actorKey, message, timeoutSeconds);
        FiniteDuration duration = Duration.create(timeoutSeconds, TimeUnit.SECONDS);
        return Await.result(future, duration);
    }

    @Override
    public <T> Future<T> sendAndGetFuture(Class actorType, Object actorId, Object message, Integer timeoutSeconds) throws Exception {
        String actorKey = actorKey(actorType, actorId);
        return sendAndGetFuture(actorKey, message, timeoutSeconds);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Future<T> sendAndGetFuture(String actorKey, Object message, Integer timeoutSeconds) throws Exception {
        if (timeoutSeconds == null) {
            timeoutSeconds = Config.getInstance().getActorResponseTimeout();
        }
        ActorRef actor = actors.get(actorKey);
        if (actor == null) {
            // actor not found
            throw MessageApi.getException("app00865",
                    "ACTOR", actorKey);
        }
        return (Future<T>) Patterns.ask(actor, message, timeoutSeconds * 1000);
    }


    @Override
    public void setActorDispatcher(Class actorType, String dispatcher) throws Exception {
        actorDispatcher.put(actorType.getName(), dispatcher);
    }

    public void startActor(Class actorType) throws Exception {
        getActorReference(actorType, null, true);
    }

    private ActorRef getActorReference(Class actorType, Object actorId, boolean createIfNotFound) throws Exception {
        String actorKey = actorKey(actorType, actorId);
        ActorRef actor = actors.get(actorKey);
        if (createIfNotFound) {
            if (actor == null) {
                actor = addActor(actorType, actorId);
            }
        }
        return actor;
    }

    @Override
    public synchronized void killAllActorsByKey(Class actorType) throws Exception {
        List<String> toKill = new LinkedList<>();

        for (String key : actors.keySet()) {
            if (isKeyForType(actorType, key)) {
                toKill.add(key);
            }
        }

        for (String actorKey : toKill) {
            killActorByKey(actorKey);
        }
    }

    private boolean isKeyForType(Class actorType, String key) {
        return key.equals(actorType.getName()) || key.startsWith(actorType.getName() + SEPARATOR);
    }

    @Override
    synchronized public void killActor(Class actorType, Object actorId) throws Exception {
        String actorKey = actorKey(actorType, actorId);
        killActorByKey(actorKey);
    }

    private void killActorByKey(String actorKey) throws Exception {
        ActorRef actor = actors.get(actorKey);
        if (actor == null) {
            // ignore if actor does not exists
            return;
        }
        HashMap<String, ActorRef> updatedActors = new HashMap<>(actors.size());
        updatedActors.putAll(actors);
        updatedActors.remove(actorKey);
        logger.debug("Actor key to kill {} ", actorKey);
        actor.tell(PoisonPill.getInstance(), ActorRef.noSender());
        actors = updatedActors;
    }

    synchronized private ActorRef addActor(Class actorType, Object actorId) throws Exception {
        String actorKey = actorKey(actorType, actorId);
        ActorRef actor = actors.get(actorKey);
        if (actor != null) {
            return actor;
        }

        if (!allowNewActors) {
            // new actors cannot be started now
            throw MessageApi.getException("app00299");
        }
        HashMap<String, ActorRef> updatedActors = new HashMap<>(actors.size() + 1);
        updatedActors.putAll(actors);
        Props props = Props.create(actorType);
        String dispatcherForLog = "default";
        String dispatcher = actorDispatcher.get(actorType.getName());
        if (dispatcher != null) {
            props = props.withDispatcher(dispatcher);
            dispatcherForLog = dispatcher;
        }
        try {
            actor = actorSystem.actorOf(props, actorKey);
        } catch (Throwable e) {
            String message = "actor add failed, existing keys are " + actors.keySet();
            throw new AppException(message, e);
        }

        updatedActors.put(actorKey, actor);
        actors = updatedActors;
        logger.debug("Created new actor of type {} with key {} dispatcher {}",
                actorType.getName(), actorKey, dispatcherForLog);

        sendToActor(actorType, actorId, new ActorInitMessage(actorKey));
        return actor;
    }

    @Override
    public List<String> getActors() throws Exception {
        LinkedList<String> result = new LinkedList<>();
        for (ActorRef actorRef : actors.values()) {
            result.add(actorRef.path().name());
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<ActorRef> getActors(Class actorType) {
        List<ActorRef> output = new LinkedList<>();

        for (String key : actors.keySet()) {
            if (isKeyForType(actorType, key)) {
                output.add(actors.get(key));
            }
        }

        return output;
    }

    @Override
    public void shutdown() {
        actorSystem.shutdown();
    }

    @Override
    public void killAllActorsByActive() throws Exception {
        for (AbstractActor actor : getActiveActors()) {
            if (!actor.isAutomaticKill()) {
                continue;
            }
            if (actor.getActorKey() == null) {
                logger.debug("actor key not initialized {}", actor.getClass().getSimpleName());
                continue;
            }
            killActorByKey(actor.getActorKey());
        }
    }

    @Override
    public void setAllowedNewActors(boolean allowed) throws Exception {
        this.allowNewActors = allowed;
    }

    @Override
    synchronized public void actorRegistration(AbstractActor actor, boolean started) {
        if (started) {
            logger.debug("actor registered {}, {}", actor.getClass().getSimpleName(), actor.pong());
            activeActors.add(actor);
        } else {
            logger.debug("actor completed {}", actor.getClass().getSimpleName());
            activeActors.remove(actor);
        }
    }

    @Override
    synchronized public Collection<AbstractActor> getActiveActors() throws Exception {
        return new HashSet<>(activeActors);
    }
}
