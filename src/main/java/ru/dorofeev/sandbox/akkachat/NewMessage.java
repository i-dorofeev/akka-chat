package ru.dorofeev.sandbox.akkachat;

import akka.actor.ActorRef;

public class NewMessage {

	final ActorRef sender;
	private final String message;

	public NewMessage(ActorRef sender, String message) {
		this.sender = sender;
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NewMessage that = (NewMessage) o;

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
		return "NewMessage{" +
			"sender=" + sender +
			", message='" + message + '\'' +
			'}';
	}
}
