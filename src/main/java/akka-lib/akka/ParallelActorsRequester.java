package akka;

import config.Config;
import logger.AppLogger;
import osgi.OsgiUtil;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ParallelActorsRequester {
    private static final AppLogger logger = AppLogger.getLogger(ParallelActorsRequester.class);

    private final ActorsManager actorsManager;
    private final HashMap<Object, Future> futures;
    private final int timeout;
    private final HashMap<Object, Object> responses;

    public ParallelActorsRequester() throws Exception {
        actorsManager = OsgiUtil.getService(ActorsManager.class);
        futures = new HashMap<>();
        responses = new HashMap<>();
        timeout = Config.getInstance().getActorResponseTimeout();
    }

    public void addRequest(Object requestKey, Class actorType, Object actorId, Object message) throws Exception {
        Future future = actorsManager.sendAndGetFuture(actorType, actorId, message, timeout);
        futures.put(requestKey, future);
    }

    public void waitForCompletion() throws Exception {
        for (Object key : futures.keySet()) {
            Future future = futures.get(key);
            FiniteDuration duration = Duration.create(timeout, TimeUnit.SECONDS);
            try {
                Object response = Await.result(future, duration);
                responses.put(key, response);
            } catch (Throwable e) {
                logger.warn("error getting response from actor " + key, e);
            }
        }
    }

    public Object getResponse(Object key) throws Exception {
        return responses.get(key);
    }
}
