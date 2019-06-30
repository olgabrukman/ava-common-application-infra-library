package messages;

public class ActorInitMessage {
    private String actorKey;

    public ActorInitMessage(String actorKey) {
        this.actorKey = actorKey;
    }

    public String getActorKey() {
        return actorKey;
    }
}
