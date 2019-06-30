package actor;

import api.ProgressId;

public class GetProgressMessage {
    private final boolean isForGui;
    private final String pattern;
    private final ProgressId filterProgress;

    public GetProgressMessage(boolean isForGui, String pattern, ProgressId filterProgress) {
        this.isForGui = isForGui;
        this.pattern = pattern;
        this.filterProgress = filterProgress;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isForGui() {
        return isForGui;
    }

    public ProgressId getFilterProgress() {
        return filterProgress;
    }
}
