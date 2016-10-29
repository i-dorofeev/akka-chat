package ru.dorofeev.sandbox.akkachat;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

public class ConversationActor extends UntypedActor {

	public static class InAddUser {
		final ActorRef user;

		public InAddUser(ActorRef user) {
			this.user = user;
		}
	}

	private final Set<ActorRef> users = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof InAddUser)
			onAddUser((InAddUser)msg);

		else if (msg instanceof NewMessage)
			onNewMessage((NewMessage)msg);

		else
			unhandled(msg);
	}

	private void onAddUser(InAddUser msg) {
		users.add(msg.user);
		getSender().tell("user added", getSelf());
	}

	private void onNewMessage(NewMessage msg) {
		users.stream()
			.filter(u -> !u.equals(msg.sender))
			.forEach(u -> u.tell(msg, getSelf()));
	}
}
