package ru.dorofeev.sandbox.akkachat.consoleapp;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jboss.netty.channel.ChannelException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ConsoleApp {

	public static void main(String[] args) throws Exception {

		Config config = ConfigFactory.load();
		config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=\"localhost\"")
			.withFallback(config);
		config = ConfigFactory.parseString("akka.remote.netty.tcp.port=\"2554\"")
			.withFallback(config);

		ActorSystem system = createActorSystem();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

			Application application = new Application(system);

			boolean exit;
			do {
				System.out.print(application.getPrompt() + "> ");
				String cmd = br.readLine();
				exit = processCmd(application, cmd);
			} while (!exit);
		} finally {
			system.terminate();
		}
	}

	private static ActorSystem createActorSystem() {
		String host = "localhost";
		int port = 2552;

		while (port < 2600) {
			try {
				Config config = ConfigFactory.load();
				config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=\"" + host  + "\"")
					.withFallback(config);
				config = ConfigFactory.parseString("akka.remote.netty.tcp.port=\"" + port + "\"")
					.withFallback(config);

				ActorSystem system = ActorSystem.create("system", ConfigFactory.load(config));
				System.out.println("Listening at " + host + ":" + port);
				return system;
			} catch (ChannelException e) {
				port++;
			}
		}

		throw new RuntimeException("Couldn't find unused port.");
	}


	private static boolean processCmd(Application app, String cmd) {
		if ("exit".equals(cmd))
			return true;

		String[] args;

		args = parseCmd(cmd, "user", 1);
		if (args != null) {
			app.switchUser(args[0]);
			return false;
		}

		args = parseCmd(cmd, "conv", 3);
		if (args != null) {
			app.switchConversation(args[0], args[1], args[2]);
		}

		args = parseCmd(cmd, "conv", 1);
		if (args != null) {
			app.switchConversation(args[0]);
			return false;
		}

		app.submitMessage(cmd);
		return false;
	}

	private static String[] parseCmd(String cmd, String cmdName, int argCount) {
		String[] args = cmd.split(" ");
		if (args.length == (argCount + 1) && args[0].equals(cmdName))
			return Arrays.copyOfRange(args, 1, args.length);
		else
			return null;
	}
}
