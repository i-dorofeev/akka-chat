package ru.dorofeev.sandbox.akkachat.core;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an active conversation between multiple users.
 */
public class ConversationActor extends UntypedActor {

	private final Set<ActorRef> users = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof AddUserCmd)
			onAddUser((AddUserCmd)msg);

		else if (msg instanceof UserActor.NewMessageMsg)
			onNewMessage((UserActor.NewMessageMsg)msg);

		else
			unhandled(msg);
	}

	private void onAddUser(AddUserCmd msg) {
		users.add(msg.user);
		getSender().tell("user added", getSelf());
	}

	private void onNewMessage(UserActor.NewMessageMsg msg) {
		if (!users.contains(getSender()))
			return;

		users.stream()
			.filter(u -> !u.equals(msg.sender))
			.forEach(u -> u.tell(msg, getSelf()));
	}


	/**
	 * Adds a user to the conversation.
	 */
	public static class AddUserCmd {
		final ActorRef user;

		/**
		 * Add a user to the conversation.
		 *
		 * @param user		A user to add.
		 */
		public AddUserCmd(ActorRef user) {
			this.user = user;
		}
	}
}
