package ru.dorofeev.sandbox.akkachat.consoleapp;

import akka.actor.ActorSystem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ConsoleApp {

	public static void main(String[] args) throws Exception {

		ActorSystem system = ActorSystem.create();

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

	private static boolean processCmd(Application app, String cmd) {
		if ("exit".equals(cmd))
			return true;

		String[] args;

		args = parseCmd(cmd, "user", 1);
		if (args != null) {
			app.switchUser(args[0]);
			return false;
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
