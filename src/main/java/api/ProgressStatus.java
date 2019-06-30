package api;

public enum ProgressStatus {
    STARTED(false),
    COMPLETED(true),
    RUNNING(false),
    FAILED(true);

    private boolean isFinal;

    ProgressStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
