package actor;

import akka.MessageHandler;
import api.ProgressId;
import logger.AppLogger;
import progress.Progress;
import progress.ProgressTimeComparator;

import java.util.Collections;
import java.util.LinkedList;

public class ProgressUpdateWorker extends MessageHandler<ProgressUpdateMessage> {
    private static final AppLogger logger = AppLogger.getLogger(ProgressUpdateWorker.class);

    private ProgressActor actor;

    @Override
    public void handle(ProgressUpdateMessage message) throws Exception {
        actor = (ProgressActor) getActor();
        updateStatus(message);
        cleanup();
    }

    private void cleanup() throws Exception {
        LinkedList<Progress> roots = actor.getRoots();
        int entriesMax = 0; //get from config
        if (roots.size() <= entriesMax) {
            return;
        }
        Collections.sort(roots, new ProgressTimeComparator());
        int remove = roots.size() - entriesMax;
        for (int removeIndex = 0; removeIndex < remove; removeIndex++) {
            Progress root = roots.get(removeIndex);
            cleanupRoot(root);
        }
    }

    private void cleanupRoot(Progress root) {
        for (ProgressId id : root.getIds()) {
            actor.getProgresses().remove(id);
        }
    }

    private void updateStatus(ProgressUpdateMessage message) {
        Progress progress = actor.getProgresses().get(message.getId());
        if (progress == null) {
            Progress parent = null;
            if (message.getParentId() != null) {
                parent = actor.getProgresses().get(message.getParentId());
                if (parent == null) {
                    logger.debug("received update for non existing parent, probably already cleaned up - ignoring");
                    return;
                }
            }
            if (!message.isCreate()) {
                logger.debug("received update for non existing progress, probably already cleaned up - ignoring");
                return;
            }
            progress = new Progress(message.getId(), message.getDescription());
            if (parent != null) {
                parent.addChild(progress);
                progress.setParent(parent);
            }
            actor.getProgresses().put(message.getId(), progress);
        }
        progress.setStatus(message.getStatus(), false, message.getException());
    }
}
