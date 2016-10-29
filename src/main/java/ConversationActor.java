import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

public class ConversationActor extends UntypedActor {

	public static class InAddUser {

		public final ActorRef user;

		public InAddUser(ActorRef user) {
			this.user = user;
		}
	}

	private final Set<ActorRef> users = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof InAddUser) {
			users.add( ((InAddUser)msg).user );
			getSender().tell("user added", getSelf());
		} else if (msg instanceof NewMessage) {
			users.stream()
				.filter(u -> u.equals( ((NewMessage)msg).sender ))
				.forEach(u -> u.tell(msg, getSelf()));
		} else {
			unhandled(msg);
		}

	}
}
