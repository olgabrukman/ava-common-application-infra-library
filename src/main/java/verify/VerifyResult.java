package verify;

import resource.MessageApi;
import time.TimeUtil;

public class VerifyResult {
    private String message;

    public VerifyResult(String id, Object... parameters) throws Exception {
        message = "[" + id + "] " + TimeUtil.getTimeCurrent(false) + " " + MessageApi.getResource(id, parameters);
    }

    public String summary() {
        return message;
    }
}
