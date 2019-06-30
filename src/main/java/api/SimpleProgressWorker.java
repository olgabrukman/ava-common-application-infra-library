package api;

public interface SimpleProgressWorker {
    void doProgressWork(ProgressId progressId, Object context) throws Exception;
}
