package progress;


import actor.GetProgressResponse;
import api.ProgressId;
import api.ProgressStatus;
import string.StringUtil;
import time.TimeUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class Progress {
    private final String description;
    private final ProgressId progressId;
    private long updateTime;
    private ProgressStatus status;
    private HashSet<Progress> children;
    private Progress parent;
    private Throwable failureException;

    public Progress(ProgressId progressId, String description) {
        this.progressId = progressId;
        if (description == null) {
            this.description = "N/A";
        } else {
            this.description = description;
        }
        status = ProgressStatus.RUNNING;
        children = new HashSet<>();
        updateTime = System.currentTimeMillis();
    }

    public void addChild(Progress child) {
        children.add(child);
    }

    public void setStatus(ProgressStatus newStatus, boolean isFromChild, Throwable e) {
        if (status == ProgressStatus.FAILED) {
            // will not be updated once failed
            return;
        }
        if (newStatus == ProgressStatus.FAILED) {
            setStatusDirect(ProgressStatus.FAILED, e);
            return;
        }
        if ((newStatus == ProgressStatus.COMPLETED) && allChildrenCompleted()) {
            setStatusDirect(ProgressStatus.COMPLETED, e);
            return;
        }
        if (isFromChild) {
            setStatusDirect(ProgressStatus.RUNNING, e);
            return;
        }
        setStatusDirect(newStatus, e);
    }

    private void setStatusDirect(ProgressStatus newStatus, Throwable e) {
        if (newStatus == status) {
            return;
        }
        updateTime = System.currentTimeMillis();
        status = newStatus;
        failureException = e;
        if (parent != null) {
            parent.setStatus(status, true, null);
        }
    }

    private boolean allChildrenCompleted() {
        for (Progress child : children) {
            if (child.status != ProgressStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }

    public void setParent(Progress parent) {
        this.parent = parent;
    }

    public Progress getParent() {
        return parent;
    }

    public String getDescription() {
        return description;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    private ProgressDetails getProgressDetails(boolean forGui, String prefix) throws Exception {
        ProgressDetails details = new ProgressDetails();
        details.setDescription(prefix + description);
        details.setStatus(status.toString());
        details.setUpdateTime(TimeUtil.getForGuiOrCli(updateTime, forGui));
        details.setError(getErrorMessage());
        return details;
    }

    private String getErrorMessage() throws Exception {
        if (failureException == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        Throwable currentError = failureException;
        while (true) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(currentError.getMessage());
            Throwable nextError = currentError.getCause();
            if (nextError == null) {
                break;
            }
            if (nextError == currentError) {
                break;
            }
            currentError = nextError;
        }
        String lines = result.toString();
        return StringUtil.breakLinesWidth(lines, 50);
    }

    public void addDetails(GetProgressResponse response, boolean forGui, String prefix) throws Exception {
        response.addDetails(getProgressDetails(forGui, prefix));
        prefix += "  ";
        LinkedList<Progress> sorted = new LinkedList<>(children);
        Collections.sort(sorted, new ProgressNameComparator());
        for (Progress child : sorted) {
            child.addDetails(response, forGui, prefix);
        }
    }

    public Collection<ProgressId> getIds() {
        LinkedList<ProgressId> ids = new LinkedList<>();
        ids.add(progressId);
        for (Progress child : children) {
            ids.addAll(child.getIds());
        }
        return ids;
    }

    public boolean matchPattern(String pattern) {
        if (pattern == null) {
            return true;
        }
        if (description.contains(pattern)) {
            return true;
        }
        for (Progress child : children) {
            if (child.matchPattern(pattern)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchId(ProgressId filterProgress) {
        return filterProgress == null || filterProgress.equals(progressId);
    }
}
