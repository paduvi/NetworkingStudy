package action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import datasegment.DataSegment;

public class Receive extends Action {

	private DataSegment packetReceived;
	private int clientPort;

	public Receive(int connectPort) {
		this.connectionPort = connectPort;
		// this.serverWelcomePort = serverPort;
		try {
			this.connectionSocket = new DatagramSocket(connectionPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void execute() throws IOException {
		byte[] clientData = new byte[1040];
		DatagramPacket clientPacket = new DatagramPacket(clientData, clientData.length);

		connectionSocket.receive(clientPacket);
		ipAddress = clientPacket.getAddress();
		this.clientPort = clientPacket.getPort();
		// System.out.println(clientPort);
		// DataSegment received = new DataSegment(clientData);
		this.packetReceived = new DataSegment(clientData);
	}

	// Send ACK to client
	public void sendACK(int ack) throws IOException {

		DataSegment synackSegment = new DataSegment(0, ack, 0, true, true, false, "".getBytes());

		// IpAddress = clientSynPacket.getAddress();
		// int clientPort = clientSynPacket.getPort();

		DatagramPacket synack = new DatagramPacket(synackSegment.toByteArray(), synackSegment.toByteArray().length,
				ipAddress, clientPort);

		connectionSocket.send(synack);
	}

	public DataSegment getPacketReceived() {
		return packetReceived;
	}

	public int getClientPort() {
		return clientPort;
	}

}
