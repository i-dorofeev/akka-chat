import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;

import static akka.testkit.JavaTestKit.duration;

public class ChatTest {

	private static final FiniteDuration TIMEOUT = duration("0.1 second");

	@Test
	public void basicScenario() {

		ActorSystem system = ActorSystem.create();

		new JavaTestKit(system) {{

			ActorRef conversationActorRef = system.actorOf(Props.create(ConversationActor.class));
			ActorRef userActorRef1 = system.actorOf(Props.create(UserActor.class), "user1");
			ActorRef userActorRef2 = system.actorOf(Props.create(UserActor.class), "user2");

			conversationActorRef.tell(new ConversationActor.InAddUser(userActorRef1), getRef());
			expectMsgEquals(TIMEOUT, "user added");

			conversationActorRef.tell(new ConversationActor.InAddUser(userActorRef2), getRef());
			expectMsgEquals(TIMEOUT, "user added");

			JavaTestKit user1Listener = new JavaTestKit(system);
			userActorRef1.tell(new UserActor.InAddNewListener(user1Listener.getRef()), getRef());
			expectMsgEquals(TIMEOUT, "listener added");

			JavaTestKit user2Listener = new JavaTestKit(system);
			userActorRef2.tell(new UserActor.InAddNewListener(user2Listener.getRef()), getRef());
			expectMsgEquals(TIMEOUT, "listener added");

			userActorRef1.tell(new UserActor.InSubmitNewMessage("Hello!", conversationActorRef), ActorRef.noSender());
			user1Listener.expectMsgEquals(TIMEOUT, new NewMessage(userActorRef1, "Hello!"));
			user2Listener.expectNoMsg(TIMEOUT);
		}};

	}
}
