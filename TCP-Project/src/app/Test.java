package app;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) {
		Thread server = new Thread(new Runnable() {
			public void run() {
				try {
					Server server = new Server("localhost", 9998, 9999);
					server.transfer();
					server.writeToFile();

				} catch (IOException e) {
					System.out.println(e);
				}
			}
		});

		Thread client = new Thread(new Runnable() {
			public void run() {
				try {
					String fileName = "123.pdf";

					Client client = new Client("localhost", 9998, 9999, fileName);
					OutputStream outputStream = client.getOutputStream();

					Path path = Paths.get(fileName);
					byte[] data = Files.readAllBytes(path);
					outputStream.write(data);
					outputStream.flush();

				} catch (IOException e) {
					System.out.println(e);
				}
			}
		});

		server.start();
		client.start();
	}

}
