package ru.dorofeev.sandbox.akkachat.consoleapp;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import ru.dorofeev.sandbox.akkachat.core.ConversationActor;
import ru.dorofeev.sandbox.akkachat.core.UserActor;

import java.util.HashMap;
import java.util.Map;

import static akka.actor.ActorRef.noSender;

class Application {

	private final ActorSystem actorSystem;
	private final ActorRef listener;

	private ActorRef currentUser;
	private ActorRef currentConversation;

	private final Map<String, ActorRef> users = new HashMap<>();
	private final Map<String, ActorRef> conversations = new HashMap<>();

	Application(ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
		this.listener = actorSystem.actorOf(Props.create(UserListenerActor.class));
	}

	void submitMessage(String msg) {
		if (currentUser != null && currentConversation != null)
			currentUser.tell(new UserActor.SubmitNewMessageCmd(msg, currentConversation), noSender());
	}

	void switchUser(String name) {
		ActorRef userRef = users.get(name);
		if (userRef == null) {
			userRef = actorSystem.actorOf(Props.create(UserActor.class), name);
			Patterns.ask(userRef, new UserActor.AddListenerCmd(listener), 1000);
			users.put(name, userRef);
		}

		currentUser = userRef;
		onStateChanged();
	}

	void switchConversation(String name) {
		ActorRef conversationRef = conversations.get(name);
		if (conversationRef == null) {
			conversationRef = actorSystem.actorOf(Props.create(ConversationActor.class), name);
			conversations.put(name, conversationRef);
		}

		currentConversation = conversationRef;
		onStateChanged();
	}

	private void onStateChanged() {
		if (currentUser != null && currentConversation != null)
			Patterns.ask(currentConversation, new ConversationActor.AddUserCmd(currentUser), 1000);
	}

	String getPrompt() {
		return getCurrentConversationName() + "/" + getCurrentUserName();
	}

	private String getCurrentUserName() {
		return "u[" + (currentUser != null ? currentUser.path().name() : "-") + "]";
	}

	private String getCurrentConversationName() {
		return "c[" + (currentConversation != null ? currentConversation.path().name() : "-") + "]";
	}
}
