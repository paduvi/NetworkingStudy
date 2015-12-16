package app;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;

import action.Handshake;
import action.Receive;
import datasegment.DataSegment;
import datasegment.SplitFile;
import utils.Buffer;
import utils.Constants;

public class Server {

	private Receive receiveAction;
	private DataSegment packet;
	private int prevPacket = -1;
	private Buffer buffer;

	private File file = new File("output.pdf");
	private FileOutputStream fileOutputStream = new FileOutputStream(file);

	public Server(String clientIp, int connectPort, int welcomePort) throws IOException {

		buffer = new Buffer(Constants.SIZE_BUFFER);
		Handshake handshake = new Handshake(clientIp, connectPort, welcomePort);
		this.receiveAction = new Receive(connectPort);
		try {
			handshake.executeServer();
		} catch (SocketException e) {
			e.getMessage();
		}
	}

	public void transfer() throws IOException {
		SplitFile f = new SplitFile(Files.readAllBytes(Paths.get("123.pdf")));
		f.splitFile();
		while (true) {
			if ((prevPacket + 1) == f.getNumPacket()) {
				break;
			}
			receiveAction.execute();
			packet = receiveAction.getPacketReceived();

			if (packet.getSeqNum() <= prevPacket) {

				// resend ack to client
				receiveAction.sendACK(packet.getSeqNum());
				System.out.println("The ack " + packet.getSeqNum() + " is RESENT");
			}

			if (packet.getSeqNum() == (prevPacket + 1)) {
				prevPacket = packet.getSeqNum();

				// save in buffer
				if (buffer.isFull()) {
					System.out.println("Buffer is FULL");
					fileOutputStream.write(buffer.getBytesAndClear());
					buffer.insert(packet.getAppData());
				} else {
					buffer.insert(packet.getAppData());
				}
				System.out.println("The packet " + packet.getSeqNum() + " is received");

				// send ack to client
				receiveAction.sendACK(packet.getSeqNum());
				System.out.println("The ack " + packet.getSeqNum() + " is sent");
			}
		}
	}

	/*
	 * public byte[] getDataFromApp() throws IOException{ return packetReceived;
	 * }
	 * 
	 * public int getIDPacket() { return idPacket; } public void
	 * setDataReceived(byte[] data) { this.dataReceived = data; } public byte[]
	 * getDataReceived() { return dataReceived; }
	 */

	public void writeToFile() throws IOException {
		if (!buffer.isEmpty()) {
			System.out.println("The Data is successfully received!");
			fileOutputStream.write(buffer.getBytesAndClear());
		}
		fileOutputStream.close();
		Desktop.getDesktop().open(file.getAbsoluteFile().getParentFile());
	}

}
