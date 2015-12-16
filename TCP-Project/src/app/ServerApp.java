package app;

import java.io.IOException;

public class ServerApp {
	
	public static void main(String[] args) throws IOException {
		Server server = new Server("localhost", 9998, 9999);
		server.transfer();
		server.writeToFile();
	}

}
