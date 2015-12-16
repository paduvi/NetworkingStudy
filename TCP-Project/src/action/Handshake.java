package action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import datasegment.DataSegment;

public class Handshake extends Action {

	public Handshake(String name) {
		super(name);
	}

	public Handshake(String ipAddress, int clientPort, int serverPort) {
		// super(nameAction);
		this.connectionPort = clientPort;
		this.serverWelcomePort = serverPort;
		try {
			this.clientSocket = new DatagramSocket();
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void execute() {

	}

	public void executeClient() throws IOException {
		// Send syn segment to the server
		DataSegment synSegment = new DataSegment(0, 0, 0, true, false, false, "".getBytes());
		// send syn segment
		sendSegment(synSegment, ipAddress, serverWelcomePort);

		// Complete three-way handshake
		DataSegment synackSeg = segmentReceived();
		if (synackSeg.isSYN() & synackSeg.isACK()) {
			DataSegment ackForWelcomeSegment = new DataSegment(1, 1, 0, true, false, false, "".getBytes());
			sendSegment(ackForWelcomeSegment, ipAddress, serverWelcomePort);

			System.out.println("Connection Established Successful!");
		}
	}

	public void executeServer() throws IOException {
		welcomeSocket = new DatagramSocket(serverWelcomePort);
		while (true) {
			byte[] clientSynData = new byte[1040];
			DatagramPacket clientSynPacket = new DatagramPacket(clientSynData, clientSynData.length);
			welcomeSocket.receive(clientSynPacket);

			DataSegment handshakingSegment = new DataSegment(clientSynData);

			if (handshakingSegment.isSYN()) {
				// send back SYNACK segment
				System.out.println("Started handshaking... at " + System.currentTimeMillis());
				DataSegment synackSegment = new DataSegment(0, 1, 0, true, true, false, "".getBytes());

				ipAddress = clientSynPacket.getAddress();
				int clientPort = clientSynPacket.getPort();

				DatagramPacket synack = new DatagramPacket(synackSegment.toByteArray(),
						synackSegment.toByteArray().length, ipAddress, clientPort);

				welcomeSocket.send(synack);
				System.out.println("Stopped handshaking at " + System.currentTimeMillis());
				break;
			}
		}
	}

	private void sendSegment(DataSegment dataSegment, InetAddress Ip, int port) throws IOException {
		DatagramPacket dataPaket = new DatagramPacket(dataSegment.toByteArray(), dataSegment.toByteArray().length, Ip,
				port);
		clientSocket.send(dataPaket);
	}

	private DataSegment segmentReceived() throws IOException {
		byte[] serverData = new byte[1040];
		DatagramPacket serverPacket = new DatagramPacket(serverData, serverData.length);
		clientSocket.receive(serverPacket);

		DataSegment receivedSegment = new DataSegment(serverData);
		return receivedSegment;
	}

}
