package ru.dorofeev.sandbox.akkachat.core;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an active (logged in) user in the system.
 *
 * The user may be involved in multiple conversations simultaneously.
 */
public class UserActor extends UntypedActor {

	private final Set<ActorRef> listeners = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof AddListenerCmd)
			onAddNewListener((AddListenerCmd) msg);

		else if (msg instanceof NewMessageMsg)
			onNewMessage((NewMessageMsg) msg);

		else if (msg instanceof SubmitNewMessageCmd)
			onSubmitNewMessage((SubmitNewMessageCmd) msg);

		else
			unhandled(msg);
	}

	private void onAddNewListener(AddListenerCmd msg) {
		listeners.add(msg.listenerRef);
		getSender().tell("listener added", getSelf());
	}

	private void onNewMessage(NewMessageMsg msg) {
		listeners.forEach(l -> l.tell(msg, getSelf()));
	}

	private void onSubmitNewMessage(SubmitNewMessageCmd msg) {
		msg.targetConversation.tell(new NewMessageMsg(getSelf(), msg.message), getSelf());
	}


	/**
	 * Submits new message to the specified conversation.
	 */
	public static class SubmitNewMessageCmd {

		final String message;
		final ActorRef targetConversation;

		/**
		 * Submits new message to the specified conversation.
		 * @param message				The body of the message.
		 * @param targetConversation	Target conversation for the message.
		 */
		public SubmitNewMessageCmd(String message, ActorRef targetConversation) {
			this.message = message;
			this.targetConversation = targetConversation;
		}
	}

	/**
	 * Adds a listener to receive notifications about the incoming messages
	 * for this user.
	 */
	public static class AddListenerCmd {

		final ActorRef listenerRef;

		/**
		 * Adds a listener to receive notifications about the incoming messages
		 * for this user.
		 *
		 * @param listenerRef Reference to the listener actor.
		 */
		public AddListenerCmd(ActorRef listenerRef) {
			this.listenerRef = listenerRef;
		}
	}

	/**
	 * Represents a new message.
	 *
	 * User may receive new messages as well as send them.
	 */
	public static class NewMessageMsg {

		final ActorRef sender;
		private final String message;

		/**
		 * A new message.
		 *
		 * @param sender		The sender of the message.
		 * @param message		The body of the message.
		 */
		public NewMessageMsg(ActorRef sender, String message) {
			this.sender = sender;
			this.message = message;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			NewMessageMsg that = (NewMessageMsg) o;
			return sender.equals(that.sender) && message.equals(that.message);

		}

		@Override
		public int hashCode() {
			int result = sender.hashCode();
			result = 31 * result + message.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "NewMessageMsg{" +
				"sender=" + sender +
				", message='" + message + '\'' +
				'}';
		}
	}
}
