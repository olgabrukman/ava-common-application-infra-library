package api;


import actor.GetProgressMessage;
import actor.GetProgressResponse;
import actor.ProgressActor;
import actor.ProgressUpdateMessage;
import akka.ActorsManager;
import osgi.OsgiUtil;
import progress.ProgressDetails;

import java.util.List;

public class ProgressApi {
    public static ProgressId create(ProgressId parent, String description) throws Exception {
        ProgressId id = ProgressId.generate();
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        ProgressUpdateMessage message = new ProgressUpdateMessage(
                parent, id, description, ProgressStatus.STARTED, true, null);
        actorsManager.sendToActorUnique(ProgressActor.class, message);
        return id;
    }

    public static void update(ProgressId id, ProgressStatus status) throws Exception {
        updateWorker(id, status, null);
    }

    public static void failure(ProgressId id, Throwable e) throws Exception {
        updateWorker(id, ProgressStatus.FAILED, e);
    }

    public static void updateWorker(ProgressId id, ProgressStatus status, Throwable e) throws Exception {
        if (id == null) {
            return;
        }
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        ProgressUpdateMessage message = new ProgressUpdateMessage(null, id, null, status, false, e);
        actorsManager.sendToActorUnique(ProgressActor.class, message);
    }

    public static List<ProgressDetails> getProgresses(boolean isForGui, String pattern, ProgressId filterProgress)
            throws Exception {
        ActorsManager actorsManager = OsgiUtil.getService(ActorsManager.class);
        GetProgressMessage message = new GetProgressMessage(isForGui, pattern, filterProgress);
        GetProgressResponse response = actorsManager.sendToActorUniqueAndReceive(ProgressActor.class, message, null);
        return response.getList();
    }

}
