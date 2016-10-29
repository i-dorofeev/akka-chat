package ru.dorofeev.sandbox.akkachat;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

public class UserActor extends UntypedActor {

	public static class InSubmitNewMessage {

		final String message;
		final ActorRef targetConversation;

		public InSubmitNewMessage(String message, ActorRef targetConversation) {
			this.message = message;
			this.targetConversation = targetConversation;
		}
	}

	public static class InAddNewListener {

		final ActorRef listenerRef;

		public InAddNewListener(ActorRef listenerRef) {
			this.listenerRef = listenerRef;
		}
	}

	private final Set<ActorRef> listeners = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof InAddNewListener)
			onAddNewListener((InAddNewListener) msg);

		else if (msg instanceof NewMessage)
			onNewMessage((NewMessage) msg);

		else if (msg instanceof InSubmitNewMessage)
			onSubmitNewMessage((InSubmitNewMessage) msg);

		else
			unhandled(msg);
	}

	private void onAddNewListener(InAddNewListener msg) {
		listeners.add(msg.listenerRef);
		getSender().tell("listener added", getSelf());
	}

	private void onNewMessage(NewMessage msg) {
		listeners.forEach(l -> l.tell(msg, getSelf()));
	}

	private void onSubmitNewMessage(InSubmitNewMessage msg) {
		msg.targetConversation.tell(new NewMessage(getSelf(), msg.message), getSelf());
	}
}
