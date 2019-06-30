package actor;

import akka.MessageHandlerWithResponse;
import progress.Progress;
import progress.ProgressTimeComparator;

import java.util.Collections;
import java.util.LinkedList;

public class GetProgressWorker extends MessageHandlerWithResponse<GetProgressMessage> {
    @Override
    protected GetProgressResponse getResponse(GetProgressMessage message) throws Exception {
        ProgressActor actor = (ProgressActor) getActor();

        LinkedList<Progress> roots = actor.getRoots();
        Collections.sort(roots, new ProgressTimeComparator());
        GetProgressResponse response = new GetProgressResponse();
        for (Progress root : roots) {
            if (root.matchPattern(message.getPattern()) &&
                    root.matchId(message.getFilterProgress())) {
                root.addDetails(response, message.isForGui(), "");
            }
        }
        return response;
    }

}
