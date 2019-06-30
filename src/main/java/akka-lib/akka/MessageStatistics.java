package akka;

public class MessageStatistics {
    private int amount;
    private int failures;
    private long lastReceiveTime;
    private long totalRunTime;

    public void update(long startTime, long endTime, Throwable workerError) {
        long runTime = endTime - startTime;
        amount++;
        lastReceiveTime = startTime;
        totalRunTime += runTime;
        if (workerError != null) {
            failures++;
        }
    }

    public MessageStatistics deepClone() {
        MessageStatistics cloned = new MessageStatistics();
        cloned.amount = this.amount;
        cloned.lastReceiveTime = this.lastReceiveTime;
        cloned.totalRunTime = this.totalRunTime;
        cloned.failures = this.failures;
        return cloned;
    }

    public int getFailures() {
        return failures;
    }

    public int getAmount() {
        return amount;
    }

    public long getLastReceiveTime() {
        return lastReceiveTime;
    }

    public long getTotalRunTime() {
        return totalRunTime;
    }
}
