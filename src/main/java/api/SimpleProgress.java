package api;

import resource.MessageApi;

public class SimpleProgress {
    private final String description;
    private final SimpleProgressWorker worker;
    private Object context;

    public SimpleProgress(SimpleProgressWorker worker, String resourceId, Object... parameters) {
        this.worker = worker;
        this.description = MessageApi.getResource(resourceId, parameters);
    }

    public void setContext(Object context) {
        this.context = context;
    }

    public void handle() throws Exception {
        ProgressId progressId = ProgressApi.create(null, description);
        try {
            ProgressApi.update(progressId, ProgressStatus.RUNNING);
            worker.doProgressWork(progressId, context);
            ProgressApi.update(progressId, ProgressStatus.COMPLETED);
        } catch (Throwable e) {
            ProgressApi.failure(progressId, e);
            throw e;
        }
    }
}
