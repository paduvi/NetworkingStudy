import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ChatServer implements Runnable {
	private ServerSocket server = null;
	private Thread thread = null;
	private List<ChatServerThread> clients = new LinkedList<ChatServerThread>();

	public ChatServer(int port) {
		try {
			System.out
					.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);
			System.out.println("Server started: " + server);
			start();
		} catch (IOException ioe) {
			System.out.println("Can not bind to port " + port + ": "
					+ ioe.getMessage());
		}
	}

	public void run() {
		while (thread != null) {
			try {
				System.out.println("Waiting for a client ...");
				addThread(server.accept());
			} catch (IOException ioe) {
				System.out.println("Server accept error: " + ioe);
				stop();
			}
		}
	}

	private void addThread(Socket socket) {
		System.out.println("Client accepted: " + socket);
		ChatServerThread chatServerThread = new ChatServerThread(this, socket);
		clients.add(chatServerThread);
		try {
			chatServerThread.open();
			chatServerThread.start();
		} catch (IOException ioe) {
			System.out.println("Error opening thread: " + ioe);
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread.stop();
			thread = null;
		}
	}

	private ChatServerThread findClient(int ID) {
		for(ChatServerThread client : clients){
			if (client.getID() == ID)
				return client;
		}
		return null;
	}

	public synchronized void handle(int ID, String input) {
		if (input.equals(".bye")) {
			findClient(ID).send(".bye");
			remove(ID);
		} else
			for(ChatServerThread client : clients){
				if (client.getID() != ID)
					client.send(ID + ": " + input);
			}
	}

	public synchronized void remove(int ID) {
		ChatServerThread client = findClient(ID);
		if (client != null) {
			System.out.println("Removing client thread " + ID + " at " + clients.indexOf(client));
			clients.remove(client);
			try {
				client.close();
			} catch (IOException ioe) {
				System.out.println("Error closing thread: " + ioe);
			}
			client.stop();
		}
	}

	public static void main(String args[]) {
		if (args.length != 1)
			System.out.println("Usage: java ChatServer port");
		else
			new ChatServer(Integer.parseInt(args[0]));
	}
}

class ChatServerThread extends Thread {
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	public ChatServerThread(ChatServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort();
	}

	public void send(String msg) {
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (IOException ioe) {
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);
			stop();
		}
	}

	public int getID() {
		return ID;
	}

	public void run() {
		System.out.println("Server Thread " + ID + " running.");
		while (true) {
			try {
				server.handle(ID, streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}
}