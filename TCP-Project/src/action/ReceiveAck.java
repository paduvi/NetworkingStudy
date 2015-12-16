package action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import datasegment.DataSegment;

public class ReceiveAck extends Action {

	private int ack;

	public ReceiveAck(DatagramSocket clientSocket) {
		// this.connectionPort = connectPort;
		this.connectionSocket = clientSocket;
	}

	public void execute() throws IOException {
		// while(true) {
		byte[] ackData = new byte[1040];
		DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length);
		connectionSocket.receive(ackPacket);

		DataSegment ackReceived = new DataSegment(ackData);
		setAck(ackReceived.getAckNum());
		// }
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getAck() {
		return ack;
	}

}
