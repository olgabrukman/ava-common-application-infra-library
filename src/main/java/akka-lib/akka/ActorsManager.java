package akka;

import akka.actor.ActorRef;
import scala.concurrent.Future;

import java.util.Collection;
import java.util.List;

public interface ActorsManager {
    void setActorDispatcher(Class actorType, String dispatcher) throws Exception;

    void startActor(Class actorType) throws Exception;

    void sendToActor(Class actorType, Object actorId, Object message) throws Exception;

    void sendToActorUnique(Class actorType, Object message) throws Exception;

    void sendToActorByPath(String actorFullPath, Object message) throws Exception;

    boolean doesActorExist(Class actorType, Object actorId) throws Exception;

    void killActor(Class actorType, Object actorId) throws Exception;

    void killAllActorsByKey(Class actorType) throws Exception;

    <T> T sendToActorUniqueAndReceive(Class actorType, Object message, Integer timeoutSeconds) throws Exception;

    <T> T sendToActorAndReceive(Class actorType, Object actorId, Object message, Integer timeoutSeconds) throws Exception;

    <T> T sendToActorByPathAndReceive(String actorFullPath, Object message, Integer timeoutSeconds) throws Exception;

    <T> Future<T> sendAndGetFuture(String actorKey, Object message, Integer timeoutSeconds) throws Exception;

    <T> Future<T> sendAndGetFuture(Class actorType, Object actorId, Object message, Integer timeoutSeconds) throws Exception;

    List<String> getActors() throws Exception;

    List<ActorRef> getActors(Class actorType);

    void shutdown() throws Exception;

    void killAllActorsByActive() throws Exception;

    void setAllowedNewActors(boolean allowed) throws Exception;

    void actorRegistration(AbstractActor abstractActor, boolean started);

    Collection<AbstractActor> getActiveActors() throws Exception;
}
