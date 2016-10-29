package ru.dorofeev.sandbox.akkachat.tests;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import ru.dorofeev.sandbox.akkachat.ConversationActor;
import ru.dorofeev.sandbox.akkachat.UserActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;

import static akka.actor.Props.create;

class ConversationTestKit extends JavaTestKit {

	private final Map<String, ActorRef> users = new HashMap<>();
	private final Map<String, JavaTestKit> userListeners = new HashMap<>();

	private final FiniteDuration timeout;

	ConversationTestKit(ActorSystem system, FiniteDuration timeout) {
		super(system);

		this.timeout = timeout;
	}

	ActorRef createConversation() {
		return getSystem().actorOf(create(ConversationActor.class));
	}


	void addUsersToConversation(ActorRef conversation, String... userNames) {
		for (String userName: userNames) {
			ActorRef user = getSystem().actorOf(create(UserActor.class), userName);
			users.put(userName, user);

			JavaTestKit userListener = createUserTestListener(user);
			userListeners.put(userName, userListener);

			addUserToConversation(conversation, user);
		}
	}

	ActorRef user(String name) {
		return users.get(name);
	}

	JavaTestKit listener(String name) {
		return userListeners.get(name);
	}


	private JavaTestKit createUserTestListener(ActorRef user) {
		JavaTestKit user1Listener = new JavaTestKit(getSystem());
		user.tell(new UserActor.InAddNewListener(user1Listener.getRef()), getRef());
		expectMsgEquals(timeout, "listener added");
		return user1Listener;
	}

	private void addUserToConversation(ActorRef conversation, ActorRef user) {
		conversation.tell(new ConversationActor.InAddUser(user), getRef());
		expectMsgEquals(timeout, "user added");
	}
}
