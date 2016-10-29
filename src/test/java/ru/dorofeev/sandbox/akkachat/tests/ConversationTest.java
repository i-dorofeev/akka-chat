package ru.dorofeev.sandbox.akkachat.tests;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.Before;
import org.junit.Test;
import ru.dorofeev.sandbox.akkachat.core.UserActor;
import scala.concurrent.duration.FiniteDuration;

import static akka.actor.ActorRef.noSender;
import static akka.testkit.JavaTestKit.duration;

public class ConversationTest {

	private static final FiniteDuration TIMEOUT = duration("0.1 second");

	private ActorSystem system;
	private ConversationTestKit kit;

	@Before
	public void initTest() {
		system = ActorSystem.create();
		kit = new ConversationTestKit(system, TIMEOUT);
	}

	@Test
	public void basicScenario() {

		// given
		ActorRef conversation = kit.createConversation();
		kit.addUsersToConversation(conversation, "user1", "user2", "user3");

		// when
		ActorRef user1 = kit.user("user1");
		user1.tell(new UserActor.SubmitNewMessageCmd("Hello!", conversation), noSender());

		// then
		kit.listener("user1").expectNoMsg(TIMEOUT);
		kit.listener("user2").expectMsgEquals(TIMEOUT, new UserActor.NewMessageMsg(user1, "Hello!"));
		kit.listener("user3").expectMsgEquals(TIMEOUT, new UserActor.NewMessageMsg(user1, "Hello!"));
	}

	@Test
	public void conversationShouldNotAcceptMessagesFromUnknownUsers() {

		// given
		ActorRef conversation = kit.createConversation();
		kit.addUsersToConversation(conversation, "user1", "user2");

		// when
		ActorRef alien = system.actorOf(Props.create(UserActor.class), "alien");
		alien.tell(new UserActor.SubmitNewMessageCmd("Hello!", conversation), noSender());

		// then
		kit.listener("user1").expectNoMsg(TIMEOUT);
		kit.listener("user2").expectNoMsg(TIMEOUT);
	}

}

