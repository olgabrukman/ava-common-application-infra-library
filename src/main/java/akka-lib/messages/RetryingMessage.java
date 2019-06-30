package messages;

public class RetryingMessage {
    private int times = 0;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
