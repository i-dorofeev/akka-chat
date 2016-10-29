import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.HashSet;
import java.util.Set;

public class UserActor extends UntypedActor {

	public static class InSubmitNewMessage {

		public final String message;
		public final ActorRef targetConversation;


		public InSubmitNewMessage(String message, ActorRef targetConversation) {
			this.message = message;
			this.targetConversation = targetConversation;
		}
	}

	public static class InAddNewListener {

		public final ActorRef listenerRef;

		public InAddNewListener(ActorRef listenerRef) {
			this.listenerRef = listenerRef;
		}
	}

	private final Set<ActorRef> listeners = new HashSet<>();

	@Override
	public void onReceive(Object msg) throws Throwable {

		if (msg instanceof InAddNewListener) {
			listeners.add( ((InAddNewListener)msg).listenerRef );
			getSender().tell("listener added", getSelf());
		} else if (msg instanceof NewMessage) {
			listeners.forEach(l -> l.tell(msg, getSelf()));
		} else if (msg instanceof InSubmitNewMessage) {
			InSubmitNewMessage cmd = (InSubmitNewMessage) msg;
			cmd.targetConversation.tell(new NewMessage(getSelf(), cmd.message), getSelf());
		} else {
			unhandled(msg);
		}
	}
}
