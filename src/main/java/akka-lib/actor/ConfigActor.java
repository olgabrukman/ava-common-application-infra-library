package actor;


import akka.AbstractActor;

public class ConfigActor extends AbstractActor {
    @Override
    protected void addHandlers() throws Exception {
        addHandler(ConfigUpdateMessage.class, ConfigUpdateWorker.class);
    }
}
