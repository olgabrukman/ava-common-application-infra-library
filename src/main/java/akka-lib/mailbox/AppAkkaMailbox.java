package mailbox;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import com.typesafe.config.Config;
import scala.Option;

public class AppAkkaMailbox implements MailboxType, ProducesMessageQueue<AppAkkaQueue> {
    public AppAkkaMailbox(ActorSystem.Settings settings, Config config) {
        //used by akka
    }

    @Override
    public MessageQueue create(Option<ActorRef> option, Option<ActorSystem> option1) {
        return new AppAkkaQueue();
    }
}
