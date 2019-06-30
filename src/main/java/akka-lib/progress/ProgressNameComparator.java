package progress;

import java.util.Comparator;

public class ProgressNameComparator implements Comparator<Progress> {
    @Override
    public int compare(Progress a, Progress b) {
        return a.getDescription().compareTo(b.getDescription());
    }
}
