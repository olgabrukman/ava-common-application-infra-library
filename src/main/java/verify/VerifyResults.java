package verify;

import java.util.LinkedList;
import java.util.List;

public class VerifyResults {
    private List<VerifyResult> results = new LinkedList<>();

    public void addAll(VerifyResults others) {
        results.addAll(others.results);
    }

    public void add(VerifyResult result) {
        results.add(result);
    }

    public void clear() {
        results.clear();
    }

    public String summary() {
        StringBuilder summary = new StringBuilder();
        for (VerifyResult result : results) {
            summary.append(result.summary());
            summary.append("\n");
        }
        return summary.toString();
    }
}
