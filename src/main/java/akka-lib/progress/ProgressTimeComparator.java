package progress;

import java.util.Comparator;

public class ProgressTimeComparator implements Comparator<Progress> {
    @Override
    public int compare(Progress a, Progress b) {
        return Long.compare(a.getUpdateTime(), b.getUpdateTime());
    }
}
