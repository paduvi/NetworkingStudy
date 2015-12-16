package app;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp {

	private static String fileName;

	public static void main(String[] args) throws Exception {
		fileName = "123.pdf";
		Client client = new Client("localhost", 9998, 9999, fileName);
		OutputStream outputStream = client.getOutputStream();

		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		outputStream.write(data);
		outputStream.flush();
	}

}
