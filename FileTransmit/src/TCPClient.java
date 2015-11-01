import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {

	public TCPClient(InetAddress address, int port) throws IOException {
		Socket client = new Socket(address, port);
		System.out.println("Connecting...");
		DataInputStream input = null;
		DataOutputStream output = null;
		File file = new File("abc.dat");
		FileInputStream reader = null;
		try {
			input = new DataInputStream(client.getInputStream());
			System.out.println("Server Say: " + input.readUTF());
			
			reader = new FileInputStream(file);
			ByteArrayOutputStream writer = new ByteArrayOutputStream();
			int read = -1;
			byte[] buffer = new byte[4 * 1024];
			while ((read = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, read);
			}
			
			output = new DataOutputStream(client.getOutputStream());
			output.write(writer.toByteArray());
			System.out.println("Send Byte completed!");
			writer.close();
		} finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
			if (reader != null)
				reader.close();
			if (client != null)
				client.close();
		}
	}

	public static void main(String[] args) throws IOException {
		InetAddress address = InetAddress.getByName("localhost");
		int port = 4567;
		new TCPClient(address, port);
	}
}
