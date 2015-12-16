package action;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class Action {

	protected String nameAction;

	protected int serverWelcomePort;
	protected int connectionPort;

	// For Client
	protected DatagramSocket clientSocket;
	protected InetAddress ipAddress;

	// For Server
	protected DatagramSocket welcomeSocket;
	protected DatagramSocket connectionSocket;

	public Action() {

	}

	public Action(String name) {
		this.nameAction = name;
	}

	public String getNameAction() {
		return nameAction;
	}

	public abstract void execute() throws IOException;

}
