package akka;

public class MessageRunData {
    private Class messageClass;
    private long startTime;

    public MessageRunData(Class messageClass) {
        this.messageClass = messageClass;
        this.startTime = System.currentTimeMillis();
    }

    public Class getMessageClass() {
        return messageClass;
    }

    public long getStartTime() {
        return startTime;
    }
}
