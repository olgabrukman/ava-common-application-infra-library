package verify;


import api.ProgressId;

public class VerifyResultsResponse {
    public static final String VERIFY_ACTOR_CLASS = "com.app.warroom.actors.verify.VerifyActor";

    private final ProgressId progressId;
    private VerifyResults results = new VerifyResults();

    public VerifyResultsResponse(ProgressId progressId) {
        this.progressId = progressId;
    }

    public void addResult(VerifyResult result) {
        results.add(result);
    }

    public ProgressId getProgressId() {
        return progressId;
    }

    public VerifyResults getResults() {
        return results;
    }
}
