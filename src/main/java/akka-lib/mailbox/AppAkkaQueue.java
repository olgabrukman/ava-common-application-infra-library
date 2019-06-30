package mailbox;

import akka.actor.ActorRef;
import akka.dispatch.Envelope;
import akka.dispatch.MessageQueue;
import akka.dispatch.UnboundedMessageQueueSemantics;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AppAkkaQueue implements MessageQueue, UnboundedMessageQueueSemantics {
    private final Queue<Envelope> priority = new ConcurrentLinkedQueue<>();
    private final Queue<Envelope> standard = new ConcurrentLinkedQueue<>();

    @Override
    public void enqueue(ActorRef actorRef, Envelope envelope) {
        if (envelope.message() instanceof AppPriorityMessage) {
            priority.offer(envelope);
        } else {
            standard.offer(envelope);
        }
    }

    @Override
    public Envelope dequeue() {
        Envelope envelope = priority.poll();
        if (envelope != null) {
            return envelope;
        }
        return standard.poll();
    }

    @Override
    public int numberOfMessages() {
        return priority.size() + standard.size();
    }

    @Override
    public boolean hasMessages() {
        return !(priority.isEmpty() && standard.isEmpty());
    }

    @Override
    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        for (Envelope handle : priority) {
            deadLetters.enqueue(owner, handle);
        }
        for (Envelope handle : standard) {
            deadLetters.enqueue(owner, handle);
        }
    }
}
