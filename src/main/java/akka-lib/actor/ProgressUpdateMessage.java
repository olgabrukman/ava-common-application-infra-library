package actor;

import api.ProgressId;
import api.ProgressStatus;

public class ProgressUpdateMessage {
    private final ProgressId parentId;
    private final ProgressId id;
    private final String description;
    private final ProgressStatus status;
    private boolean create;
    private Throwable exception;

    public ProgressUpdateMessage(ProgressId parentId, ProgressId id, String description, ProgressStatus status,
                                 boolean create, Throwable exception) {
        this.parentId = parentId;
        this.id = id;
        this.description = description;
        this.status = status;
        this.create = create;
        this.exception = exception;
    }

    public boolean isCreate() {
        return create;
    }

    public ProgressId getParentId() {
        return parentId;
    }

    public ProgressId getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "ProgressUpdateMessage{" +
                "parentId=" + parentId +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", create=" + create +
                ", exception=" + exception +
                '}';
    }
}
