package actor;

import akka.AbstractActor;
import api.ProgressId;
import progress.Progress;

import java.util.HashMap;
import java.util.LinkedList;

public class ProgressActor extends AbstractActor {
    private HashMap<ProgressId, Progress> progresses = new HashMap<>();

    @Override
    protected void addHandlers() throws Exception {
        addHandler(ProgressUpdateMessage.class, ProgressUpdateWorker.class);
        addHandler(GetProgressMessage.class, GetProgressWorker.class);
        addHandler(ProgressResetMessage.class, ProgressResetWorker.class);
    }

    public HashMap<ProgressId, Progress> getProgresses() {
        return progresses;
    }

    public LinkedList<Progress> getRoots() {
        LinkedList<Progress> roots = new LinkedList<>();
        for (Progress progress : progresses.values()) {
            if (progress.getParent() == null) {
                roots.add(progress);
            }
        }
        return roots;
    }

}
