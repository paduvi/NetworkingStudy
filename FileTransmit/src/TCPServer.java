import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

	private ServerSocket server;

	public TCPServer(int port) throws IOException {
		server = new ServerSocket(port);
		System.out.println("Running...");
		while (true) {
			Socket client = server.accept();
			System.out.println(client.getInetAddress() + " has connected");
			new Thread() {
				@Override
				public void run() {
					DataInputStream input = null;
					DataOutputStream output = null;
					File file = new File("D:/download.jpg");
					FileOutputStream writer = null;
					try {
						output = new DataOutputStream(client.getOutputStream());
						output.writeUTF("I'm a socket server");
						
						input = new DataInputStream(client.getInputStream());
						writer = new FileOutputStream(file);
						int read = -1;
						byte[] buffer = new byte[4 * 1024];
						while ((read = input.read(buffer)) != -1) {
							writer.write(buffer, 0, read);
						}
						Desktop.getDesktop().open(file);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (input != null)
								input.close();
							if (output != null)
								output.close();
							if (writer != null)
								writer.close();
							if (client != null)
								client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}

	public static void main(String[] args) throws IOException {
		int port = 4567;
		new TCPServer(port);
	}
}
