package ru.dorofeev.sandbox.akkachat.consoleapp;

import akka.actor.UntypedActor;
import ru.dorofeev.sandbox.akkachat.core.UserActor;

class UserListenerActor extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof UserActor.NewMessageMsg) {
			onNewMessage((UserActor.NewMessageMsg) msg);
		} else {
			unhandled(msg);
		}
	}

	private void onNewMessage(UserActor.NewMessageMsg msg) {
		System.out.println(getSender().path().name() + " [from " + msg.getSender().path().name() + "]: " + msg.getMessage());
	}
}
